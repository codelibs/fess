# -*- coding:utf-8 -*-
require 'net/http'
#require 'uri'
require 'rexml/document'
require 'json'
require 'rest_client'
require 'optparse'

#
# parsing command line options
#
options = {}

optparse = OptionParser.new do |parser|
  parser.on('-f lang', '--from lang', 'Input language can be \'ja\', \'en\',.. (Required)') {|v| options[:from] = v}
  parser.on('-t lang', '--to lang', 'Output language can be \'ja\', \'en\',.. (Required)') {|v| options[:to] = v}
  parser.on('-i [file]', '--input [file]', 'Input file (Optional)') {|v| options[:input] = v}
  parser.on('-o [file]', '--output [file]', 'Output file (Optional)') {|v| options[:output] = v}
  parser.on('-v', '--verbose', 'Verbose message to STDERR (Optional)') {|v| options[:verbose] = v}
  parser.on('-d', '--dryrun', 'Dry-run (don\'t translate) (Optional)') {|v| options[:dryrun] = v}
end

begin
  optparse.parse!
  mandatory = [:from, :to]
  missing = mandatory.select{ |param| options[param].nil? }
  if not missing.empty?
    STDERR.puts "Missing options: #{missing.join(', ')}"
    STDERR.puts optparse
    exit
  end
rescue OptionParser::InvalidOption, OptionParser::MissingArgument
  STDERR.puts $!.to_s
  STDERR.puts optparse
  exit
end

$from = options[:from]
$to = options[:to]

if (options[:input] == nil)
  $input = STDIN
else
  $input = open(options[:input])
end

if (options[:output] == nil)
  $output = STDOUT
else
  $output = open(options[:output], "w")
end

$verbose = options[:verbose]
$dryrun = options[:dryrun]

#
# Translator class
#
class Translator
  CLIENT_ID       = 'Set Client ID'
  CLIENT_SECRET   = 'Set Client Secret Key'
  AUTHORIZE_URL   = 'https://datamarket.accesscontrol.windows.net/v2/OAuth2-13'
  TRANSLATION_URL = 'http://api.microsofttranslator.com/V2/Http.svc/Translate'
  SCOPE           = 'http://api.microsofttranslator.com'

  @@access_token = nil

  def get_access_token
    unless @@access_token == nil
      return @@access_token
    end
    json = JSON.parse(
      RestClient.post(AUTHORIZE_URL,
        {
          'grant_type' => 'client_credentials',
          'client_id' => CLIENT_ID,
          'client_secret' => CLIENT_SECRET,
          'scope' => SCOPE,
        },
        :content_type => 'application/x-www-form-urlencoded'
      )
    )
    @@access_token = json['access_token']
    @@access_token
  end
  private:get_access_token

  def translate(text, from, to)
    access_token = get_access_token
    unless $dryrun
      xml = REXML::Document.new(
        RestClient.get("#{TRANSLATION_URL}?from=#{from}&to=#{to}&text=#{URI.escape(text)}",
          'Authorization' => "Bearer #{access_token}"
        )
      )
      xml.root.text
    else
      "..."
    end
  end
end

#
# Extends REXML::Element class
#
class REXML::Element
  def has_cdata?
    self.cdatas.length > 0
  end
end 

#
# translateNode
#
def translateNode(element)

  translator = Translator.new

  # Translate attributes
  if (element.is_a?(REXML::Element))
    if (element.has_attributes?)
      $attributes.each do |attribute|
        text = element.attributes[attribute]
        if /\S+/ =~ text
          unless (text.nil? || text.empty?)
            STDERR.puts "attributes[#{attribute}]=#{text}" if $verbose
            element.attributes[attribute] = translator.translate(text, $from, $to)
            STDERR.print "." unless $verbose
            STDERR.puts " =>#{element.attributes[attribute]}" if $verbose
          end
        end
      end
    end

    # Translate recursively if has children
    if (element.has_elements?)
      element.map.each do |child|
         translateNode(child)
      end
      return
    end

    # Noting to do if CDATA
    if (element.has_cdata?)
      return
    end
  end

  # Translate the text
  if (element.is_a?(REXML::Text))
    text = element.value
    if /\S+/ =~ text
      unless (text.nil? || text.empty?)
        STDERR.puts "text=#{text}" if $verbose
        element.value = translator.translate(text, $from, $to)
        STDERR.print "." unless $verbose
        STDERR.puts " =>#{element.value}" if $verbose
      end
    end
  elsif (element.is_a?(REXML::Element) && element.has_text?)
    text = element.text
    if /\S+/ =~ text
      unless (text.nil? || text.empty?)
        STDERR.puts "text=#{text}" if $verbose
        element.text = translator.translate(text, $from, $to)
        STDERR.print "." unless $verbose
        STDERR.puts " =>#{element.text}" if $verbose
      end
    end
  end
end


#
# parsing xml and translate (main)
#
$attributes = Array["name", "alt", "content"]

doc = REXML::Document.new($input)
translateNode(doc.root)
$output.puts doc.to_s

STDERR.print "\n" unless $verbose
