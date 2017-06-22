
def call(path, outputFile) {
	sh "curl -v --fail --user 'admin:admin123' -o \"${outputFile}\" \"http://nexus:8081/repository/raw/${path}\""
}
