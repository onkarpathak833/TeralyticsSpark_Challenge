### Probelm Statement

*** Given a movement dataset of people in a building, predict whether two persons identified by UID have met or not? ***

### Assumptions :

1. Timezone/Timestamp is in GMT; however it is set as configurable through application.conf file
2. x and y cordinates of person's movement are in meters considering its an indoor building setup

### Definition of a meeting :

1. Since two person cannot be at the exact same x and y co-ordinate when they see/meet each other, it is assumed that two people within
distance of 1 meter from each other may have met each other

e.g. UID with co-ordinates (x,y) can meet UID with possible co-ordinates (x+1,y), (x,y+1), (x-1,y), (x, y-1), (x+1,y+1), (x-1, y-1),
(x+1, y-1), (x-1, y+1).

All these co-ordinates are within approx 1 meter distance of person with location (x, y) hence they could have met each other.

2. Also, two person can be at a distance of 1 meter from each other at same time accurate till seconds but not till micro and nano seconds

So, two person are considered to be met when they are at a distance of around 1 meter from each other at the same time calculated in seconds.

#
### BUILD STEPS

# Code requires following setup to run :

1. Oracle JDK 1.8
2. Apache Spark 2.3.0
3. Scala 2.11.8
4. Sbt version 1.3

Build the project from sbt shell by using commands :

1. sbt clean compile
2. sbt assembly (for fat/uber jar)
3. sbt package (for thin jar)

I recommend using fat jar for this setup. Fat jar will be created in your target folder of around 125 KB.

# RUN program

# Program Arguments

-config <path to application.conf file> => application.conf file specify the path of your dataset i.e. reduced.csv in this case.
Update application.conf file with correct path on your system. If not specified it will use default application.conf file in source resources folder.

-uid1 => string value of first UID you want to search in dataset

-uid2  => string value of second UID you want to find in dataset

NOTE : while running your spark application in local mode specify "spark_env" property in application.conf file as "local"

# RUN COMMAND

spark-submit --class "com.example.project.MyApp" <path_to_your_uberJar> -config <path_to_application.conf> -uid1 <UID1> -uid2 <UID2>

 <specify master env in case running on actual cluster using --master flag>

e.g.
spark-submit --master local --class "com.example.project.MyApp" /Users/techops/Documents/MyProjects/target/scala-2.11/MyProjects-assembly-0.1.jar -config /Users/techops/Documents/MyProjects/src/main/resources/application.conf -uid1 27cb9c6f -uid2 78b85537

# SAMPLE OUTPUT

case 1: if two UID's meet each other :

nts/MyProjects/src/main/resources/application.conf -uid1 27cb9c6f -uid2 78b85537
/Users/techops/Documents/Spark/spark-2.2.1-bin-hadoop2.6/conf/spark-env.sh: line 56: SPARK_WORKER_INSTANCES: command not found
/Users/techops/Documents/Spark/spark-2.2.1-bin-hadoop2.6/conf/spark-env.sh: line 57: SPARK_WORKER_CORES: command not found
UID1 : 27cb9c6f and UID2 :78b85537 may have met each other at following meeting points 46 times
+-------------------+------------+-----------------+------------+--------+-------------------+------------+-----------------+--------+
|uidOneDateTime     |uidOneXValue|uidOneYValue     |meetingFloor|uidOne  |uidTwoDateTime     |uidTwoXValue|uidTwoYValue     |uidTwo  |
+-------------------+------------+-----------------+------------+--------+-------------------+------------+-----------------+--------+
|2014-07-19 21:30:05|103.7674    |71.47382163072028|2           |27cb9c6f|2014-07-19 21:30:05|103.461044  |71.42452175621781|78b85537|
|2014-07-19 21:30:07|103.7826    |71.49702169194096|2           |27cb9c6f|2014-07-19 21:30:07|103.79914   |71.7109623418192 |78b85537|
|2014-07-19 21:30:11|103.783646  |71.49760966117029|2           |27cb9c6f|2014-07-19 21:30:11|104.13205   |71.68077214092389|78b85537|
|2014-07-19 21:58:06|103.774216  |71.4785581012154 |2           |27cb9c6f|2014-07-19 21:58:06|103.202     |70.82631312396587|78b85537|
|2014-07-19 22:31:05|103.7745    |71.51971133920388|2           |27cb9c6f|2014-07-19 22:31:05|103.84089   |71.71662140687206|78b85537|
|2014-07-19 22:32:07|103.84768   |71.69116037249775|2           |27cb9c6f|2014-07-19 22:32:07|103.5683    |71.98575950337947|78b85537|
|2014-07-19 22:39:17|103.76918   |71.4482959966818 |2           |27cb9c6f|2014-07-19 22:39:17|103.841415  |71.39757660947834|78b85537|
|2014-07-19 23:00:06|103.859505  |71.65959592349105|2           |27cb9c6f|2014-07-19 23:00:06|103.88793   |71.66302631132328|78b85537|
|2014-07-19 23:00:30|103.8167    |71.65104448520788|2           |27cb9c6f|2014-07-19 23:00:30|103.86751   |71.32013564627711|78b85537|
|2014-07-19 23:02:11|103.75489   |71.42726900130533|2           |27cb9c6f|2014-07-19 23:02:11|103.81955   |71.54297467083111|78b85537|
|2014-07-19 23:04:05|103.78099   |71.58847414806579|2           |27cb9c6f|2014-07-19 23:04:05|103.8972    |71.84567581501696|78b85537|
|2014-07-19 23:20:32|103.77458   |71.458142828458  |2           |27cb9c6f|2014-07-19 23:20:32|104.11116   |71.73725743424241|78b85537|
|2014-07-19 23:24:11|103.77308   |71.45465262798243|2           |27cb9c6f|2014-07-19 23:24:11|104.047775  |71.41032706203404|78b85537|
|2014-07-19 23:24:13|103.784515  |71.51522357628122|2           |27cb9c6f|2014-07-19 23:24:13|104.16084   |71.65233761260751|78b85537|
|2014-07-19 23:27:05|103.77555   |71.45010263274447|2           |27cb9c6f|2014-07-19 23:27:05|103.83948   |71.44003843361139|78b85537|
|2014-07-19 23:30:41|103.776024  |71.56043735557259|2           |27cb9c6f|2014-07-19 23:30:41|104.242935  |71.41284556707834|78b85537|
|2014-07-19 23:33:22|103.79017   |71.49188534510904|2           |27cb9c6f|2014-07-19 23:33:22|103.92253   |71.14184163265769|78b85537|
|2014-07-19 23:33:23|103.78963   |71.49220186837856|2           |27cb9c6f|2014-07-19 23:33:23|103.64893   |70.88757801297331|78b85537|
|2014-07-19 23:57:12|103.77458   |71.458142828458  |2           |27cb9c6f|2014-07-19 23:57:12|103.74266   |71.42943152040337|78b85537|
|2014-07-20 00:00:53|103.783195  |71.49666189372563|2           |27cb9c6f|2014-07-20 00:00:53|102.92304   |70.69422492286213|78b85537|
+-------------------+------------+-----------------+------------+--------+-------------------+------------+-----------------+--------+
only showing top 20 rows...


case 2 : if 2 UID's dont meet each other :

nts/MyProjects/src/main/resources/application.conf -uid1 600dfbe2 -uid2 5e7b40e1
/Users/techops/Documents/Spark/spark-2.2.1-bin-hadoop2.6/conf/spark-env.sh: line 56: SPARK_WORKER_INSTANCES: command not found
/Users/techops/Documents/Spark/spark-2.2.1-bin-hadoop2.6/conf/spark-env.sh: line 57: SPARK_WORKER_CORES: command not found
UID1 : 600dfbe2 and UID2 : 5e7b40e1 may not meet each other
+--------------+------------+------------+------------+------+--------------+------------+------------+------+
|uidOneDateTime|uidOneXValue|uidOneYValue|meetingFloor|uidOne|uidTwoDateTime|uidTwoXValue|uidTwoYValue|uidTwo|
+--------------+------------+------------+------------+------+--------------+------------+------------+------+
+--------------+------------+------------+------------+------+--------------+------------+------------+------+