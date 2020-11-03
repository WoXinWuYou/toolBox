/** 时间处理工具类  */


/*日期加减
	不更新旧值；
*/
function addDate(date, dadd){  
	date = date.valueOf();
	date = date + dadd * 24 * 60 * 60 * 1000;
	return new Date(date);  
}