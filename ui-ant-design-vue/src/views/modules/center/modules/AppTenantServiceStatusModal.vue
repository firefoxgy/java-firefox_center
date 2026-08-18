<template>
  <a-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态">
          <a-switch checkedChildren="启用" unCheckedChildren="禁用" v-model="stateSwitch"/>
        </a-form-item>   
        
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  
  export default {
    name: "center_TenantModal",
    components: {      
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"应用的租户",
        width:800,
        visible: false, 
        model: {
        },
        stateSwitch:true, //状态 默认启用
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules:{
        },
        saveType: '',
        url: {
          save: "/sys/center/app/updateAppTenantService"
        }  
      }
    },
    created () {      

    },
    methods: {
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);      
        if(record.hasOwnProperty("id")){
          this.stateSwitch=record.relStatus==1?true:false;
        }
        this.visible = true;
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证        
        this.form.validateFields((err, values) => {
          if (!err) {  
            that.confirmLoading = true;
            let httpurl =this.url.save;
            let method = 'post';
            let formData ={};
            formData.appId=this.model.appId;
            formData.tenantId=this.model.tenantId;
            formData.centerId=this.model.centerId;
            formData.status=0;
            if(this.stateSwitch==true){
              formData.status=1;
            }
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.msg);
                this.$emit('ok',formData);                
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
      }
    }
  }
</script>
