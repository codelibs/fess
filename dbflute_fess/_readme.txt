Directory for DBFlute client

manage.bat(sh) => 21 (jdbc):
A execution command of JDBC task
which gets your schema info and saves it to SchemaXML
located to the "schema" directory.
This task should be executed after ReplaceSchema task
and before other tasks(e.g. Generate, Document task).

manage.bat(sh) => 22 (doc):
A execution command of Document task
which creates documents, for example, SchemaHTML, HistoryHTML
to the "output/doc" directory.

manage.bat(sh) => 23 (generate):
A execution command of Generate task
which generates classes corresponding your tables,
for example, entities, condition-beans to specified
directories by DBFlute properties on "dfprop" directory.

Generated structures (directories and classes) are like this:
/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
allcommon : classes bridging to DBFlute Runtime
bsbhv     : base behaviors
bsentity  : base entities
cbean     : condition-beans (both base and extended)
exbhv     : extended behaviors
exentity  : extended entities
- - - - - - - - - -/

For example, if a table called "MEMBER" exists,
you can use these classes like this:
/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
memberBhv.selectEntity(cb -> {
    cb.query().setMemberId_Equal(3);
}).alwaysPresent(member -> {
    ... = member.getMemberName();
});
// memberBhv      : Behavior (instance)
// MemberCB(cb)   : ConditionBean
// Member(member) : Entity
- - - - - - - - - -/

manage.bat(sh) => 24 (sql2entity):
A execution command of Sql2Entity task
which generates classes corresponding your outside-SQL files,
for example, entities, parameter-beans to specified
directories by DBFlute properties on "dfprop" directory.

manage.bat(sh) => 0 (replace-schema):
A execution command of ReplaceSchema task
which creates your tables and loads data by
resources located to the "playsql" directory.

manage.bat(sh) => 25 (outside-sql-test):
A execution command of OutsideSqlTest task
which executes outside-SQL files and you can check
whether the SQLs have correct formats.

The directories are for DBFlute tasks:
/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
dfprop     : Directory for DBFlute properties
extlib     : Directory for Directory for library extension
log        : Directory for log files of DBFlute tasks
output/doc : Directory for auto-generated documents
playsql    : Directory for ReplaceSchema task
schema     : Directory for files of schema info
- - - - - - - - - -/

The files, _project.bat, _project.sh, build.properties
are for internal processes of DBFlute tasks so basically
you don't need to touch them.
