
def call(file, path) {
	sh "curl -v --fail --user 'admin:admin123' --upload-file \"${file}\" \"http://nexus:8081/nexus/repository/raw/${path}\""
}
