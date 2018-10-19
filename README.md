# Logmetric
Logmetric is log transformation tool from huge log data to small-size log metrics

# Use case
- Count log lines which includes specific log pattern (ex) Count response codes from web server's access code
- Count log field values when field value is string type (ex) Count event values from event log
- Sum log field values when field value is integer type

# Key concepts
- Transform huge log data to small-size log metrics and save your resources (like storage, network, ...)
- Support for various log format (ex) Support various access log from Apache, Nginx, ...

# Configuration
- input
  - kafka topic
- output
  - kafka topic
- duration: metric creation period
