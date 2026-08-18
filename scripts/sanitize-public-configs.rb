#!/usr/bin/env ruby
# frozen_string_literal: true

root = File.expand_path('..', __dir__)
config_files = Dir.glob(File.join(root, 'firefox-cloud', '**', '*.{yml,yaml,properties}'))
                  .reject { |path| path.include?('/target/') || path.include?('/i18n/') }

credential_key = /(username|password|passwd|secret|access[-_.]?key|secret[-_.]?key|app[-_.]?(id|key|secret)|token)/i
endpoint_key = /(server-addr|jdbc[-_.]?url|datasource[-_.]?url)/i

def environment_name(key)
  normalized = key.gsub(/([a-z0-9])([A-Z])/, '\\1_\\2')
                  .upcase
                  .gsub(/[^A-Z0-9]+/, '_')
                  .gsub(/^_+|_+$/, '')
  "FIREFOX_#{normalized}"
end

config_files.each do |path|
  original = File.binread(path)
  updated = original.each_line.map do |line|
    next line if line.lstrip.start_with?('#')

    match = line.match(/\A(\s*)([^#:=]+?)(\s*[:=]\s*)(.*?)(\r?\n)?\z/)
    next line unless match

    indent, raw_key, separator, value, newline = match.captures
    key = raw_key.strip
    private_endpoint = value.match?(/\b(?:10|192\.168|172\.(?:1[6-9]|2[0-9]|3[01]))(?:\.\d{1,3}){2,3}\b/)
    sensitive = key.match?(credential_key) || key.match?(endpoint_key)
    sensitive ||= key.match?(/\A(url|host)\z/i) && value.match?(/jdbc:|\b\d{1,3}(?:\.\d{1,3}){3}\b/i)
    sensitive ||= private_endpoint
    next line unless sensitive
    next line if (value.match?(/\$\{[A-Z0-9_]+(?::[^}]*)?\}/) && !private_endpoint) || value.strip.empty?

    "#{indent}#{raw_key}#{separator}${#{environment_name(key)}:}#{newline}"
  end.join

  File.binwrite(path, updated) if updated != original
end
