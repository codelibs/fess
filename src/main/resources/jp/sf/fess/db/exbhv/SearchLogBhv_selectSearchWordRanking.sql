-- #df:entity#

-- !df:pmb extends SPB!
-- !!Timestamp fromRequestedTime!!
-- !!Timestamp toRequestedTime!!

/*IF pmb.isPaging()*/
select
-- ELSE select count(*) from (select
/*END*/
  SEARCH_WORD as name,
  count(SEARCH_WORD) as cnt
 from SEARCH_LOG 
 /*BEGIN*/where
   /*IF pmb.fromRequestedTime != null*/
   REQUESTED_TIME >= /*pmb.fromRequestedTime*/'2009-10-26 00:00:00'
   /*END*/
   /*IF pmb.toRequestedTime != null*/
   and REQUESTED_TIME < /*pmb.toRequestedTime*/'2009-10-30 00:00:00'
   /*END*/
 /*END*/
 group by SEARCH_WORD
/*IF pmb.isPaging()*/
 order by count(SEARCH_WORD) desc
-- ELSE ) as tb1
/*END*/
