import { USER_AUTH,SYS_BUTTON_AUTH } from "@/store/mutation-types"

export function authFilter(code, type, formData) {
  if(nodeAuth(code, type,formData)){
    return true;
  }else{
    return globalAuth(code, type);
  }
}

function nodeAuth(code, type,formData){
  let permissionList = [];
  try {
    if (formData) {
      let permissionList = formData.permissionList;
    }else{
      return false;
    }
  } catch (e) {}
  if (permissionList.length ==  0) {
    return false;
  }
  let permissions = [];
  for (let item of permissionList) {
    if(type=='1'){//未授权时禁用
      permissions.push(item.action);
    }else{
      if(item.type == type) {
        permissions.push(item.action);
      }
    }    
  }
  return permissions.includes(code);
}

function globalAuth(code, type){
  let permissionList = JSON.parse(sessionStorage.getItem(USER_AUTH) || "[]");
  let allPermissionList = JSON.parse(sessionStorage.getItem(SYS_BUTTON_AUTH) || "[]");
  //设置全局配置是否有命中
  let  gFlag = false;//未启用
  let permissions = [];
  if(allPermissionList != null && allPermissionList != "" && allPermissionList != undefined && allPermissionList.length > 0){
    for (let itemG of allPermissionList) {
      if(code === itemG.action && itemG.status == '0'){
        gFlag = true;
        break;
      }
    }
  }
  if(gFlag){
    return type=='1'?true:false;
  }
  if (permissionList === null || permissionList === "" || permissionList === undefined||permissionList.length<=0) {
    return type=='1'?false:true;
  }
  for (let item of permissionList) {
    permissions.push(item.action);
  }
  if(type=='1'){
    if(allPermissionList != null && allPermissionList != "" && allPermissionList != undefined && allPermissionList.length > 0){
      for (let itemG of allPermissionList) {
        if(code === itemG.action && itemG.type == '2'){
          permissions.push(itemG.action);
        }
      }
    }
    return permissions.includes(code);
  }else{
    var flag=false;
    for (let item of permissionList) {
      if(code === item.action){
        if( item.type == '1' || item.type==type){
          flag=true;
        }          
      }
    }
    return !flag;
  }
}

export function colAuthFilter(columns,pre) {
  let authList = getNoAuthCols(pre);
  const cols = columns.filter(item => {
    if (hasColoum(item,authList)) {
      return true
    }
    return false
  })
  return cols
}

/**
 * 【子表行编辑】实现两个功能：
 * 1、隐藏JEditableTable无权限的字段
 * 2、禁用JEditableTable无权限的字段
 * @param columns
 * @param pre
 * @returns {*}
 */
export function colAuthFilterJEditableTable(columns,pre) {
  let authList = getAllShowAndDisabledAuthCols(pre);
  const cols = columns.filter(item => {
    let oneAuth = authList.find(auth => {
      return auth.action === pre + item.key;
    });
    if(!oneAuth){
      return true
    }

    //代码严谨处理，防止一个授权标识，配置多次
    if(oneAuth instanceof Array){
      oneAuth = oneAuth[0]
    }

    //禁用逻辑
    if (oneAuth.type == '2' && !oneAuth.isAuth) {
      item["disabled"] = true
      return true
    }
    //隐藏逻辑逻辑
    if (oneAuth.type == '1' && !oneAuth.isAuth) {
      return false
    }
    return true
  })
  return cols
}


function hasColoum(item,authList){
  if (authList.includes(item.dataIndex)) {
    return false
  }
  return true
}

//权限无效时不做控制，有效时控制，只能控制 显示不显示
//根据授权码前缀获取未授权的列信息
function getNoAuthCols(pre){
  let permissionList = [];
  let allPermissionList = [];

  //let authList = Vue.ls.get(USER_AUTH);
  let authList = JSON.parse(sessionStorage.getItem(USER_AUTH) || "[]");
  for (let auth of authList) {
    //显示策略，有效状态
    if(auth.type == '1'&&startWith(auth.action,pre)) {
      permissionList.push(substrPre(auth.action,pre));
    }
  }
  //console.log("页面禁用权限--Global--",sessionStorage.getItem(SYS_BUTTON_AUTH));
  let allAuthList = JSON.parse(sessionStorage.getItem(SYS_BUTTON_AUTH) || "[]");
  for (let gauth of allAuthList) {
    //显示策略，有效状态
    if(gauth.type == '1'&&gauth.status == '1'&&startWith(gauth.action,pre)) {
      allPermissionList.push(substrPre(gauth.action,pre));
    }
  }
  const cols = allPermissionList.filter(item => {
    if (permissionList.includes(item)) {
      return false;
    }
    return true;
  })
  return cols;
}



/**
 * 额外增加方法【用于行编辑组件】
 * date: 2020-04-05
 * author: scott
 * @param pre
 * @returns {*[]}
 */
function getAllShowAndDisabledAuthCols(pre){
  //用户拥有的权限
  let userAuthList = JSON.parse(sessionStorage.getItem(USER_AUTH) || "[]");
  //全部权限配置
  let allAuthList = JSON.parse(sessionStorage.getItem(SYS_BUTTON_AUTH) || "[]");

  let newAllAuthList = allAuthList.map(function (item, index) {
    let hasAuthArray = userAuthList.filter(u => u.action===item.action );
    if (hasAuthArray && hasAuthArray.length>0) {
      item["isAuth"] = true
    }
    return item;
  })

  return newAllAuthList;
}

function startWith(str,pre) {
  if (pre == null || pre == "" || str==null|| str==""|| str.length == 0 || pre.length > str.length)
    return false;
  if (str.substr(0, pre.length) == pre)
    return true;
  else
    return false;
}

function substrPre(str,pre) {
  return str.substr(pre.length);
}
