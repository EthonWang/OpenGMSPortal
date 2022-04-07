const sectionData = [{    "label": "1 Model Invoking",    "children": [{        "label": "1.1 Basic information of the model",        "children": []    }, {"label": "1.2 Model' s states and events", "children": []}, {        "label": "1.3 Model data configuration",        "children": []    }, {"label": "1.4 Case loading", "children": []}, {"label": "1.5 Model running", "children": []}]}, {    "label": "2 Data Process Invoking",    "children": [{"label": "2.1 Basic Information of Data Method", "children": []}, {        "label": "2.2 Data configuration ",        "children": []    }, {"label": "2.3 Service operation", "children": []}, {        "label": "2.4 Running resultsand visualization",        "children": []    }]}, {"label": "3 Integrated operation", "children": []}]const supportDoc = [{    "title": "1 Model Invoking",    "content": "<p>If the type of calculation model is OpenGMS Model-Service Package, after the model deployment package is deployed, you can click the \" Invoke \" button on the calculation model entry page to enter the model call page.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image152.jpg\" ></p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image154.jpg\" ></p>\n<p>The model call page is divided into the following sections:</p>\n"}, {    "title": "1.1 Basic information of the model",    "content": "<p align=\"center\"><img src=\"/static/img/helpNew/image155.png\" ></p>\n<p>The left side of the page is the display of the basic information of the calculation model. You can view the name, introduction, contributors and contribution time of the calculation model. Clicking the round button below the model name will display a guide frame on the page to explain the functions of each part of the page.</p>\n"}, {    "title": "1.2 Model' s states and events",    "content": "<p>The right side column of the page is the display of the model running process and input and output data. Click the button in the upper right corner to switch the display style, and show the model running process in \" Classic \", \" Semantics \" and \" Work flow \" styles. The running process of a model can be divided into multiple states, and each state can be divided into multiple input and output events.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image157.jpg\" ></p>\n"}, {    "title": "1.3 Model data configuration",    "content": "<p>You will need to enter the event data (corresponding to confer with the format of the event as a must item), the data format in the corresponding event has name identification after which data need to select the file type from your personal data center, click on the event behind to Open the personal data center, you need to upload the required data to the personal data center before you can select the data as input data; for numeric data, you need to fill in the corresponding value type in the input box: <img src=\"/static/img/helpNew/image158.png\" ></p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image160.jpg\" ></p>\n<p>File type input event</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image162.jpg\" ></p>\n<p>Numeric type input event</p>\n"}, {    "title": "1.4 Case loading",    "content": "<p>Click the Load Test button in the left column to bring up a case selection dialog box, in which you can select the test data that was placed when loading the model package or the case data that was successfully run and shared by others.</p>\n"}, {    "title": "1.5 Model running",    "content": "<p>After configuring all the data required for the model to run, click the Load Test button under the model name in the left column, and click the \" Invoke \" button in the left column to start calling the model online. After the model starts running, you can view the running status of the model and obtain input and output data in the Task in the User Space.</p>\n"}, {    "title": "2 Data Process Invoking",    "content": "<p>After the data method is created, enter the data method details page and click the Invoke button on the display page to enter the calling page. The interface also displays the source of the service, whether it comes from the platform or the user's contribution.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image164.png\" ></p>\n"}, {    "title": "2.1 Basic Information of Data Method",    "content": "<p> On the task page of the data call, you can see some information about the service. Including service name, input data information, parameter information and output data part. And you can load test data and call services on this page. The page looks like this:</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image166.png\" ></p>\n"}, {    "title": "2.2 Data configuration ",    "content": "<p> There are two ways to run the service. One is to load test data provided by the platform, and the other is to select data to run in the personal data space.</p>\n<p> The first is the first way. Load the test data, fill in the parameters, and then click the Invoke button to call the service.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image168.png\" ></p>\n<p> The second method requires uploading the required data in the personal data space and selecting the data.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image170.png\" ></p>\n"}, {    "title": "2.3 Service operation",    "content": "<p> After data selection is complete, fill in the parameters and click the Invoke button to invoke the service.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image172.png\" ></p>\n"}, {    "title": "2.4 Running resultsand visualization",    "content": "<p> After the service is executed, a download link can be provided. Click the download link to download the processing result data after the operation is completed.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image174.png\" ></p>\n<p>After the visualization service is executed, a visualization button is provided. Click the visualization button to view the visualization results.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image176.png\" ></p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image178.png\" ></p>\n"}, {    "title": "3 Integrated operation",    "content": "<p>OpenGMS provides you with the function of integrated model operation.</p>\n<p>Due to the complexity of geographic phenomena, multiple model integrations are often required to simulate a complex geographic phenomenon. Therefore, we provide you with the function of integrated model operation. After you log in to the OpenGMS platform, enter the user space ( ueser space ), on the user space homepage, click Integrated Modeling in the quick tool card to enter the function page. You can also directly visit <a href=\"/computableModel/integratedModel\" target=\"_blank\">integratedModel</a> to use this function.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image180.jpg\" ></p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image182.png\" ></p>\n<p>The page layout is as follows:</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image184.jpg\" ></p>\n<p>1 : Integrated task execution area.</p>\n<p>2 : View operation area.</p>\n<p>3 : Integrated task step selection area.</p>\n<p>4 : Integrated task drawing canvas.</p>\n<p>5 : Data bus.</p>\n<p>6 : Display area for the currently selected integration task execution steps and all user integration tasks</p>\n<p>The following takes a specific integration task as an example to illustrate how to operate these areas.</p>\n<p>First select the integration steps you need from the leftmost area 3, these steps include start ( start ), condition judgment ( condition ), calculation model ( model ), data processing ( data method ), and termination ( end ).</p>\n<table align=\"center\" class=\"MsoTableGrid\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\"> <tbody><tr> <td valign=\"top\"> <p align=\"center\"><img src=\"/static/img/helpNew/image186.png\" ></p>\n </td> <td valign=\"top\"> <p align=\"center\"><img src=\"/static/img/helpNew/image188.png\" ></p>\n </td> <td > <p align=\"center\"><img src=\"/static/img/helpNew/image190.png\" ></p>\n </td> <td > <p align=\"center\"><img src=\"/static/img/helpNew/image192.png\" ></p>\n </td> <td valign=\"top\"> <p align=\"center\"><img src=\"/static/img/helpNew/image194.png\" ></p>\n </td> </tr> <tr> <td valign=\"top\"> <p> start</p>\n </td> <td valign=\"top\"> <p align=\"center\">condition</p>\n </td> <td valign=\"top\"> <p align=\"center\">model</p>\n </td> <td valign=\"top\"> <p align=\"center\">data method</p>\n </td> <td valign=\"top\"> <p align=\"center\">end</p>\n </td> </tr></tbody></table><p>Click the step bar to switch the module display category.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image196.png\" ></p>\n<p>Click \" Model \" to switch to the display sidebar of calculation model entries. You can search by keywords and switch the model classification.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image198.png\" ><img src=\"/static/img/helpNew/image200.png\" ></p>\n<p>Click the module name to view the details of the calculation model.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image202.png\" ></p>\n<p>Click Add to Graph in the dialog box or on the module to add this step to area 4 for drawing organization of the integrated task.<img src=\"/static/img/helpNew/image204.png\" ></p>\n<p>Click \" Data Method \" to switch to display data processing items. Similarly, you can also search, select categories and view details, and add the data processing methods you need to area 4.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image206.png\" ><img src=\"/static/img/helpNew/image208.png\" ></p>\n<p>Here we select the model Stand2Trees_csv and the data processing method csv2shp to add to the area 4 integrated task drawing canvas.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image210.png\" ></p>\n<p>Click the module to view the input and output of this model step or data processing step and the parameters that may be required</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image212.png\" ></p>\n<p>Select the one you need and add it to the canvas.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image214.png\" ></p>\n<p>Manually connect the output of the previous model with the input of the next data processing to complete a simple integration task step organization, and you can view the details of this connection.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image216.png\" ><img src=\"/static/img/helpNew/image218.png\" ></p>\n<p>Each output is added to the canvas / output will be at the bottom of the page data bus ( Data Bus shown).</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image220.png\" ></p>\n<p>Click the condition judgment to add a judgment module to the canvas. Connect it to an output. Please note that the conditional judgment only supports the output of numeric type ( int, float ) or character type ( string ).</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image222.png\" ></p>\n<p>Click the config button on the right sidebar to configure the entire condition judgment.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image224.jpg\" ></p>\n<p>Select the type of judgment object, and then click the Add button to add judgment conditions.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image226.jpg\" ><img src=\"/static/img/helpNew/image228.jpg\" ></p>\n<p>The final result is shown in the figure:</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image230.jpg\" ><img src=\"/static/img/helpNew/image232.jpg\" ></p>\n<p>When organizing integration tasks, you can click the buttons in area 2 to help you better draw the various steps. The functions of these two buttons are to switch views and full screen.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image233.png\" ></p>\n<p>After you add a calculation model or data processing method, you can view the selected steps in area 6.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image235.jpg\" ></p>\n<p>Here you can configure each step.</p>\n<p>Click the Configuration button of the model step to configure, you can fill in the description of the model step, and set the run type of the model step. If you want this model to iterate by itself, select \" iterative \" and set the number of iterations.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image237.png\" ></p>\n<p>Next is the important part of the integration task: configuring the data required for each step.</p>\n<p>Click the Load/Check Data button, you can see all the inputs and outputs of the current step. Click to configure the corresponding input. You can select the data you want from the personal data space, personal data service local nodes, and Data Item items.<img src=\"/static/img/helpNew/image239.png\" ></p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image241.png\" ><img src=\"/static/img/helpNew/image243.png\" ><img src=\"/static/img/helpNew/image245.png\" ></p>\n<p>The configured data will be displayed in the corresponding input.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image247.png\" ></p>\n<p>You can also click the data module in the canvas to view the details of the data, and click config to configure this data separately.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image249.png\" ><img src=\"/static/img/helpNew/image251.png\" ></p>\n<p>After configuring the integration task, you can click save to save the integration task. This function requires you to log in before you can use it. After saving successfully, the configuration can be used next time.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image253.jpg\" ><img src=\"/static/img/helpNew/image255.png\" ></p>\n<p>You can also directly click the execute button in area 1 in the upper left corner to run the integration task. When each integration task runs, it will be automatically saved as your personal integration task configuration.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image257.jpg\" ></p>\n<p>If the data configuration is complete, click Execute to run the integration task immediately.</p>\n<p>The running step will turn to yellow, the running step will turn to green, and the running step will turn to red.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image259.jpg\" ><img src=\"/static/img/helpNew/image261.jpg\" ><img src=\"/static/img/helpNew/image263.png\" ></p>\n<p>You can view the output and download the completed steps. If it is a data visualization step, you can also preview the processing results.</p>\n<p align=\"center\"><img src=\"/static/img/helpNew/image265.png\" ></p>\n<p>And the result of this run will be saved in your corresponding task, you can view it later.</p>\n"}]