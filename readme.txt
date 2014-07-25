This is inhouse project.

IgWebCms
Ig-Igwebcms

--------------------------------------------------------------------------------------------------------
Maven command for building the project :-

mvn clean install -P "Profile"

[Profiles = "auto-deploy" or "deploy-content(Profile for deploying content)"  ]

--------------------------------------------------------------------------------------------------------

For running the groovy scripts in felix we need to upload a bundle in felix :-

After running the above command once upload the groovy-all bundle in felix console loacted in directory
[/home/ankit/.m2/repository/org/codehaus/groovy/groovy-all/groovy-all-2.1.6.jar] in linux .

After uploading the above bundle start/activate the bundle .And groovy script will start working in felix

--------------------------------------------------------------------------------------------------------
Column Control usage :-

We need to add following css in the column control for upto 6 columns:

2;cq-colctrl-lt0	2 Columns (50%, 50%)
3;cq-colctrl-lt1	3 Columns (33%, 33%,34)
4;cq-colctrl-lt3	4 Columns (22%, 22%,22,22)
5;cq-colctrl-lt4	5 Columns (16.8% each)
6;cq-colctrl-lt5	6 Columns (13.33% each)

--------------------------------------------------------------------------------------------------------

