# Use a base image with Java (Spark requires Java)
FROM spark:3.5.5-scala2.12-java17-python3-ubuntu

WORKDIR /wytasoft_training_academy

# Set Spark environment variables
ENV SPARK_HOME=/opt/spark
ENV PATH=$SPARK_HOME/bin:$PATH

# Copy the Spark application JAR file to the container
COPY ./build/libs/wtskayansparkall-1.0.0-SNAPSHOT.jar /wytasoft_training_academy/my-spark-app.jar

RUN mkdir -p /tmp/spark-events

EXPOSE 4040 8080
# Set the default command to run the Spark application
CMD ["spark-submit", "--class", "com.wts.kayan.app.job.MainDriver", "--master", "local[*]", "/wytasoft_training_academy/my-spark-app.jar", "prd"]
