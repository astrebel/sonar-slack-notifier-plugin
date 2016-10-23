### This plugin is not continued... ###
Check out https://github.com/kogitant/sonar-slack-notifier-plugin for sonarqube 5.5+

# sonar-slack-notifier-plugin
SonarQube plugin for sending notifications to Slack

This plugin sends notifications to a given slack channel. You can configure a webhook, slack user and slack channel as a global or project specific setting.

## Howto ##
To build the plugin call **mvn clean package** (or download the current release). The artifact must be copied to the *SONAR_HOME/extensions/plugins* folder and sonarqube must be restarted.

Tested for sonarqube 5.2 and 5.3
