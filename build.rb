#! /usr/bin/env ruby
# coding: utf-8

require "fileutils"
require "nokogiri"

def sh(command)
  puts "⇒ #{command}"
  system(command) || raise("command failed")
end

name = "nekodtd"
readme_url = "https://github.com/sparklemotion/nekodtd"
build_file = "build-dtd.xml"
pom_file = "pom.xml"
generated_jar = "./#{name}.jar"
bundle_dir = "bundle"
ant_version = "1.10.13"

# check version
build_xml = File.read(build_file)
build_doc = Nokogiri::XML::Document.parse(build_xml)
build_version_node = build_doc.at_xpath("/project/property[@name='version']")
build_version = build_version_node["value"]

pom_xml = File.read(pom_file)
pom_doc = Nokogiri::XML::Document.parse(pom_xml)
pom_version_node = pom_doc.at_xpath("/project/version")
pom_version = pom_version_node.content

raise "Fix versions #{build_version} vs #{pom_version}" unless build_version == pom_version
puts "building #{build_version}"

# install ant
ant_dir = "apache-ant-#{ant_version}"
ant_bin_dir = File.join(ant_dir, "bin")
ant_bin = File.join(ant_bin_dir, "ant")
unless Dir.exist?(ant_dir) && Dir.exist?(ant_bin_dir) && File.exist?(ant_bin)
  ant_tar = "apache-ant-#{ant_version}-bin.tar.gz"
  ant_url = "https://dlcdn.apache.org/ant/binaries/#{ant_tar}"
  sh "curl -O #{ant_url}"
  sh "tar -xzf #{ant_tar}"
end

# build in a java 8 container
image_name = "jruby:9.4-jdk8"
command = <<~SH
  docker run -it \
    --mount=type=bind,source=#{Dir.pwd},target=/nekodtd-mount \
    --workdir=/nekodtd-mount \
    #{image_name} \
    #{ant_bin} -f #{build_file}
SH
sh command

# assemble files
target_name = "#{name}-#{build_version}"
FileUtils.rm_rf(bundle_dir, verbose: true)
FileUtils.mkdir_p(bundle_dir, verbose: true)
FileUtils.cp(pom_file, bundle_dir, verbose: true)
FileUtils.cp(generated_jar, File.join(bundle_dir, "#{target_name}.jar"), verbose: true)

File.open(File.join(bundle_dir, "README"), "w") { |f| f.write readme_url }
Dir.chdir(bundle_dir) do
  sh "jar -cvf #{target_name}-javadoc.jar README"
  sh "jar -cvf #{target_name}-sources.jar README"
end

# sign files
Dir.chdir(bundle_dir) do
  sh "gpg -ab #{target_name}.jar"
  sh "gpg -ab #{target_name}-sources.jar"
  sh "gpg -ab #{target_name}-javadoc.jar"
  sh "gpg -ab #{pom_file}"
end

# bundle
Dir.chdir(bundle_dir) do
  sh "jar -cvf bundle.jar #{target_name}*.jar* #{pom_file}*"
end
