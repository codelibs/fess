Directory for ReplaceSchema task

replace-schema-*.sql:
DDL statements for creation of your schema.
You should write your own DDL statements in this file.
(A SQL separator is semicolon ";")

take-finally-*.sql:
SQL statements for check loaded data (or DDL after data loading)
You should write your own SQL statements in this file.
(basically same specifications as replace-schema.sql)

The "data" directory is for data loading like this:
/- - - - - - - - - - - - - - - - - - - -
playsql
  |-data
     |-common
     |  |-xls
     |     |-10-master.xls
     |     |-defaultValueMap.dataprop
     |-ut
        |-xls
           |-20-member.xls  
           |-30-product.xls  
           |-defaultValueMap.dataprop
- - - - - - - - - -/

The format of a xls file is like this:
/- - - - - - - - - - - - - - - - - - - -
|MEMBER_ID|MEMBER_NAME|BIRTHDATE |
|        1|Stojkovic  |1965/03/03|
|        2|Savicevic  |          |
|        3|...        |...       |

(Sheet)
MEMBER / MEMBER_LOGIN / MEMBER_SECURITY
- - - - - - - - - -/

The defaultValueMap.dataprop is for common columns like this:
/- - - - - - - - - - - - - - - - - - - -
map:{
    ; REGISTER_DATETIME = sysdate
    ; REGISTER_USER     = foo
    ; REGISTER_PROCESS  = bar
    ; UPDATE_DATETIME   = sysdate
    ; UPDATE_USER       = foo
    ; UPDATE_PROCESS    = bar
    ; VERSION_NO        = 0
}
- - - - - - - - - -/
