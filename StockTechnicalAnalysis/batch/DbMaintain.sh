#MySQL Check command
#mysqlcheck -u SuperUser -ppassword --databases StockTechnicalAnalysis

#MySQL Optimize command
#mysqlcheck -u SuperUser -ppassword --optimize --databases StockTechnicalAnalysis

#MySQL Analyze command
#mysqlcheck -u SuperUser -ppassword --analyze --databases StockTechnicalAnalysis

#MySQL Backup database
#mysqldump -u $SuperUser -pppassword --databases StockTechnicalAnalysis > FILENAME

#!/bin/bash
#----------------------------------------
# OPTIONS
#----------------------------------------
USER='SuperUser'        # MySQL User
PASSWORD='password' 	# MySQL Password
DAYS_TO_KEEP=70    		# 0 to keep forever
BACKUP_PATH='/Project/StockTechnicalAnalysis/backup'
DBNAME='StockTechnicalAnalysis'
#----------------------------------------

# Create the backup folder
if [ ! -d $BACKUP_PATH ]; then
  mkdir -p $BACKUP_PATH
fi


 
date=$(date -I)
echo "Backing up database [$DBNAME] with compression"
mysqldump -u $USER -p$PASSWORD --databases $DBNAME | gzip -c > $BACKUP_PATH/$date-$DBNAME.gz

# Delete old backups
if [ "$DAYS_TO_KEEP" -gt 0 ] ; then
  echo "Deleting backups older than $DAYS_TO_KEEP days"
  find $BACKUP_PATH/* -mtime +$DAYS_TO_KEEP -exec rm {} \;
fi