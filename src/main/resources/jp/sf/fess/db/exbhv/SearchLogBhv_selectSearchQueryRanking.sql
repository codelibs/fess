-- #df:entity#

-- !df:pmb extends SPB!
-- !!Timestamp fromRequestedTime!!
-- !!Timestamp toRequestedTime!!

/*IF pmb.isPaging()*/
select
-- ELSE select count(*) from (select
/*END*/
  SEARCH_FIELD_LOG.VALUE as name,
  count(SEARCH_FIELD_LOG.VALUE) as cnt
 from SEARCH_FIELD_LOG inner join SEARCH_LOG on SEARCH_FIELD_LOG.SEARCH_ID = SEARCH_LOG.ID
 where
   SEARCH_FIELD_LOG.NAME = 'query'
   /*IF pmb.fromRequestedTime != null*/
   and SEARCH_LOG.REQUESTED_TIME >= /*pmb.fromRequestedTime*/'2009-10-26 00:00:00'
   /*END*/
   /*IF pmb.toRequestedTime != null*/
   and SEARCH_LOG.REQUESTED_TIME < /*pmb.toRequestedTime*/'2009-10-30 00:00:00'
   /*END*/
 group by SEARCH_FIELD_LOG.VALUE
/*IF pmb.isPaging()*/
 order by count(SEARCH_FIELD_LOG.VALUE) desc
-- ELSE ) as tb1
/*END*/