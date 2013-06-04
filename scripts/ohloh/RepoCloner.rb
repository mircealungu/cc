#!/usr/bin/env ruby

# single threaded solution

require 'fileutils'
require 'pty'
require 'expect'
require 'optparse'
require 'tmpdir'

WAIT_TIME = 180

def cloneRepos
	FileUtils.remove_entry_secure($repo_path, true)
	FileUtils.mkdir_p($repo_path)
	
	while (gets)
		process_line $_
	end
end

def process_line line
	humanReadableName, name, type, repo = line.split(/\t/)
	
	begin
		humanReadableName = humanReadableName.to_s
		type = type.to_s
		name = name.gsub(/[^A-Za-z]/, "")
		repo = repo.gsub(/\r/,"").gsub(/\n/, "").split(/ /)[0]
		repoName = repo.slice(/.*\/(.+)/, 1)
		$stderr.puts "Processing #{name}: #{repo}"
		
		if name.empty?
			$stderr.puts "Empty name on #{repo}"
			return
		end
		
		folder_name = "#{$repo_path}/#{name}"
		
		if type.include? "Git"
			spawn "git clone --quiet --bare #{repo} #{folder_name}"
		elsif type.include? "Subversion"
			Dir.mktmpdir {|tmpDir|
				ok = spawn("svn co --quiet #{repo} #{tmpDir}")
				return unless ok
				
				datestamp = Time.now.strftime("%Y-%m-%d")
				%x(cd #{tmpDir} && \
					git init . && \
					git add . && \
					git commit -m "snapshot on #{datestamp}" && \
					git tag -a "head" -m "head" && \
					git gc --aggressive && \
					mv .git #{folder_name})
				$stderr.puts "Finished #{name}"
			}
		end
	rescue Exception => e
		$stderr.puts e.message
		$stderr.puts e.backtrace.inspect
	end
end

def spawn(cmd)
	$stderr.puts cmd
	begin
		PTY.spawn(cmd) do |reader, writer, pid|
			reader.expect(/name|pass/i, WAIT_TIME) { |name|
				Process.kill("TERM", pid)
				if name
					$stderr.puts("Was asked for username/password by #{cmd}")
				else
					$stderr.puts("Ran into timeout on #{cmd}")
				end
				return false
			}
			Process.kill('TERM', pid)
		end
	rescue Errno::EIO
		# That’s ok. It just means the child died before we could read it.
	end
	return true
end

if __FILE__ == $0
	# Default setting for command-line option
	$repo_path = "/tmp/repos"
	OptionParser.new do |opts|
		opts.on("--repo_path PATH",
			"Where you'd like to find the git repos once we're done.") do |path|
			$repo_path = path
		end
	end.parse!
	
	cloneRepos
end