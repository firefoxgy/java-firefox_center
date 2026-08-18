<template>      
  <a-modal
    :width="width"
    :visible="visible"
    title="选择租户"
    @ok="handleSubmit"
    @cancel="handleCancel"
    cancelText="关闭"
    wrapClassName="ant-modal-cust-warp"
    >
    <!--部门树-->
    <template>
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="租户">
          <a-tree       
            checkable
            allowClear="true"
            treeCheckable="tree"        
            :checkStrictly="true"
            v-model="checkedKeys"
            :selectedKeys="selectedKeys"
            :expandedKeys="expandedKeysss"                   
            :treeData="deviceTree"
            @check="onCheck"
            @expand="onExpand"     
            placeholder="请选择要添加到应用的租户"
            :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
            >
          </a-tree>
        </a-form-item>
      </a-form>
    </template>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import { queryAppTenantTreeList } from '@/api/api'
  export default {
    name: "center_AppTenantTree",
    data () {
      return {
        title:"操作",
        width:580,
        visible: false,        
        model:{},    
        deviceTree:[],
        allKeys: [],
        checkedKeys: [],
        selectedKeys: [],
        expandedKeysss:[],
        tenantIds:[],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        checkModeSwitch:false,
        confirmLoading: false,
        headers:{},
        form:this.$form.createForm(this),
        url: {
          save: "/sys/center/app/saveAppTenant"
        }
      }
    },
    methods: {
      show (appId) {
        this.model.appId=appId;
        this.queryAppTenantTreeList(appId);
        this.form.resetFields();
        this.visible = true;
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.allKeys=[];
        this.checkedKeys=[];
        this.expandedKeysss=[];
      },
      handleSubmit () {
        const that = this;
        if(!this.tenantIds || this.tenantIds.length==0){
          that.$message.warning('请选择要授权的租户');
          return false;
        }
        // 触发表单验证        
        this.form.validateFields((err, values) => {
          if (!err) {  
            that.confirmLoading = true;
            let httpurl =this.url.save;
            let method = 'post';
            this.model.tenantIds=this.tenantIds.join(',');
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.msg);
                this.$emit('ok',this.model);                
                that.close();
              }else{
                that.$message.warning(res.msg);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }         
        })
      },
      handleCancel () {
        this.close()
      },
      onModeChange(checked) {
        if(checked){
          this.checkedKeys=this.allKeys;
        }else{
          this.checkedKeys=[];          
        }
        this.tenantIds=this.checkedKeys;
      },
      // 选择部门时作用的API
      onCheck(checkedKey, info){
        this.tenantIds = [];
        let checkedNodes = info.checkedNodes;
        for (let i = 0; i < checkedNodes.length; i++) {
          this.tenantIds.push(checkedNodes[i].data.props.id);
        }
      },
      queryAppTenantTreeList(appId){
        const that = this;
        queryAppTenantTreeList({appId:appId}).then((res)=>{
          if(res.success){
            that.deviceTree = res.data;
            that.allKeys=[];
            if(that.checkModeSwitch){
              that.getAllKey(res.data)
            }            
            that.checkedKeys=that.allKeys;
            this.tenantIds=this.checkedKeys;
          }else{
            that.$message.warning(res.msg);
          }
        })
      },
      getAllKey(items){
        const that = this;
        items.forEach((item, index) => {
          that.allKeys.push(item.key)
          if(item.children!=null){
            that.getAllKey(item.children)
          }
        })
        
      },
      onExpand(expandedKeys){
        this.expandedKeysss = expandedKeys;
      },
      handleTreeSelect(selectedKeys, event) {
        this.selectedKeys = selectedKeys
      }
    },
  }
</script>

<style scoped>
  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }
</style>