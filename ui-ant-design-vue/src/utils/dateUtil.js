
//获得某月的天数
function getMonthDays(myMonth){
  let now = new Date(); //当前日期
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //
  var monthStartDate = new Date(nowYear, myMonth, 1);
  var monthEndDate = new Date(nowYear, myMonth + 1, 1);
  var days = (monthEndDate - monthStartDate)/(1000 * 60 * 60 * 24);
  return days;
}
  
//获得本季度的开始月份
function getQuarterStartMonth(){
  let now = new Date(); //当前日期
  let nowMonth = now.getMonth(); //当前月
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //

  var quarterStartMonth = 0;
  if(nowMonth<3){
    quarterStartMonth = 0;
  }
  if(2<nowMonth && nowMonth<6){
    quarterStartMonth = 3;
  }
  if(5<nowMonth && nowMonth<9){
    quarterStartMonth = 6;
  }
  if(nowMonth>8){
    quarterStartMonth = 9;
  }
  return quarterStartMonth;
}

export function getWeekStartDate() {
  let now = new Date(); //当前日期
  let nowDayOfWeek = now.getDay(); //今天本周的第几天
  let nowDay = now.getDate(); //当前日
  let nowMonth = now.getMonth(); //当前月
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //
  return new Date(nowYear, nowMonth, nowDay - nowDayOfWeek+1);
}

export function getWeekEndDate() {
  let now = new Date(); //当前日期
  let nowDayOfWeek = now.getDay(); //今天本周的第几天
  let nowDay = now.getDate(); //当前日
  let nowMonth = now.getMonth(); //当前月
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //

  let lastMonthDate = new Date(); //上月日期
  lastMonthDate.setDate(1);
  lastMonthDate.setMonth(lastMonthDate.getMonth()-1);
  return new Date(nowYear, nowMonth, nowDay + (6 - nowDayOfWeek)+1);
}

export function getMonthStartDate() {
  let now = new Date(); //当前日期
  let nowMonth = now.getMonth(); //当前月
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //

  let lastMonthDate = new Date(); //上月日期
  lastMonthDate.setDate(1);
  lastMonthDate.setMonth(lastMonthDate.getMonth()-1);
  return new Date(nowYear, nowMonth, 1);
}

export function getMonthEndDate() {
  let now = new Date(); //当前日期
  let nowMonth = now.getMonth(); //当前月
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //

  let lastMonthDate = new Date(); //上月日期
  lastMonthDate.setDate(1);
  lastMonthDate.setMonth(lastMonthDate.getMonth()-1);
  return new Date(nowYear, nowMonth, getMonthDays(nowMonth));
}

export function getQuarterStartDate() {
  let now = new Date(); //当前日期
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //

  let lastMonthDate = new Date(); //上月日期
  lastMonthDate.setDate(1);
  lastMonthDate.setMonth(lastMonthDate.getMonth()-1);
  return new Date(nowYear, getQuarterStartMonth(), 1);
}

export function getQuarterEndDate() {
  let now = new Date(); //当前日期
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //

  var quarterEndMonth = getQuarterStartMonth() + 2;
  return new Date(nowYear, quarterEndMonth, getMonthDays(quarterEndMonth));
}

export function getYearStartDate() {
  let now = new Date(); //当前日期
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //

  return new Date(nowYear, 0, 1);
}

export function getYearEndDate() {
  let now = new Date(); //当前日期
  let nowYear = now.getYear(); //当前年
  nowYear += (nowYear < 2000) ? 1900 : 0; //
  
  return new Date(nowYear, 11, 31);
}