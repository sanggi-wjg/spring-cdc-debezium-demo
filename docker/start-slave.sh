# [Master]
show master status;

# [Slave]
STOP SLAVE;
# container ip 변경
CHANGE MASTER TO MASTER_HOST='cdc-demo-mysql-master', MASTER_USER='root', MASTER_PASSWORD='rootroot', MASTER_LOG_POS=157, MASTER_LOG_FILE='mysql-bin.000003';

START SLAVE;
SHOW SLAVE STATUS \G;