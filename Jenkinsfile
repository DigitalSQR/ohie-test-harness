node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    def mvn = tool 'Default Maven';
    dir('backend') { 
      withSonarQubeEnv() {
        sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=Testing-Harness-Tool -Dsonar.projectName='Testing Harness Tool'"
      }
    }
  }
}
