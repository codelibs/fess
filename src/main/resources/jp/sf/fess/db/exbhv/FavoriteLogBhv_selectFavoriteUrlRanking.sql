-- #df:entity#

-- !df:pmb extends SPB!
-- !!Timestamp fromCreatedTime!!
-- !!Timestamp toCreatedTime!!

/*IF pmb.isPaging()*/
select
-- ELSE select count(*) from (select
/*END*/
  URL as name,
  count(URL) as cnt
 from FAVORITE_LOG 
 /*BEGIN*/where
   /*IF pmb.fromCreatedTime != null*/
   CREATED_TIME >= /*pmb.fromCreatedTime*/'2009-10-26 00:00:00'
   /*END*/
   /*IF pmb.toCreatedTime != null*/
   and CREATED_TIME < /*pmb.toCreatedTime*/'2009-10-30 00:00:00'
   /*END*/
 /*END*/
 group by URL
/*IF pmb.isPaging()*/
 order by count(URL) desc
-- ELSE ) as tb1
/*END*/
