-- #df:entity#

-- !df:pmb extends SPB!
-- !!String url!!

/*IF pmb.isPaging()*/
select
-- ELSE select count(*) from (select
/*END*/
  URL as name,
  count(URL) as cnt
 from FAVORITE_LOG 
 /*BEGIN*/where
   /*IF pmb.url != null*/
   URL = /*pmb.url*/'http://'
   /*END*/
 /*END*/
 group by URL
/*IF pmb.isPaging()*/
 order by count(URL) desc
-- ELSE ) as tb1
/*END*/
