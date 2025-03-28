require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name           = package['authorname']
  s.version        = package['version']
  s.summary        = package['summary']
  s.description    = package['description']
  s.author         = package['author']
  s.license        = package['license']
  s.homepage       = package['homepage']
  s.source         = { :git => 'https://github.com/bnnsoftvn/react-native-biometrics-sic', :tag => "#{s.version}" }
  s.platform       = :ios, '10.0'
  s.source_files   = 'ios/**/*.{h,m}'
  s.dependency     'React-Core'
end
