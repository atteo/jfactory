
def call(path, outputFile) {
	sh "curl -s --fail --user 'admin:admin123' -o \"${outputFile}\" \"http://nexus:8081/nexus/repository/raw/${path}\""
}
