# FAME (Feedback Acquisition and Monitoring Enabler) is a Feedback Gathering and Monitoring Tool of [![SUPERSEDE](images/SUPERSEDE-logo.png)](https://www.supersede.eu/) project

This prototype enables end-users to communicate their feedback about a service or application in a flexible and multi-modal way. This means that end-users can document their feedback using text input, audio recording, ratings and screenshot annotations. The developed solution is available for Android and web applications and deployed on the SUPERSEDE platform.

The feedback gathering and the monitoring tool is composed by seven components:

- *Android Library*: Android library to include feedback gathering mechanisms in an Android application ([README.md](https://github.com/supersede-project/monitor_feedback/blob/master/android_library/README.md))
- *Monitor Manager*: (https://github.com/supersede-project/monitor_feedback/tree/master/monitor_manager)
- *Monitors*: This project contains the set of implemented monitors (RESTful web services) for the feedback gathering project ([README.md](https://github.com/supersede-project/monitor_feedback/blob/master/monitors/README.md))
- *Orchestrator*: Is a RESTfull Web API that provides endpoints for storing, receiving and updating the feedback configuration ([README.md](https://github.com/supersede-project/monitor_feedback/blob/master/orchestrator/README.md))
- *Repository*: Is a RESTfull Web API that provides endpoints for storing, receiving and deleting user feedbacks ([README.md](https://github.com/supersede-project/monitor_feedback/blob/master/repository/README.md))
- *Web Library*: jQuery Plugin to include Feedback Gathering on a Website ([README.md](https://github.com/supersede-project/monitor_feedback/blob/master/web_library/README.md))

*Please check the respective README.md for details.*

The Feedback Acquisition and Monitoring Enabler is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Main contact: Norbert Seyff <norbert.seyff@fhnw.ch>

![Project funded by the European Union](images/european.union.logo.png)  
