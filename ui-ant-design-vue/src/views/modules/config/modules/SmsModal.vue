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
          
        <a-form-item label="短信签名" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'signName', validatorRules.signName]" placeholder="请输入短信签名"></a-input>
        </a-form-item>
        <a-form-item label="描述" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea v-decorator="[ 'detail', {}]" placeholder="请输入描述" :auto-size="{ minRows: 3, maxRows: 5 }" />
        </a-form-item>
        <a-form-item label="app_key" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'appKey', validatorRules.appKey]" placeholder="请输入app_key"></a-input>
        </a-form-item>
        <a-form-item label="app_secret" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'appSecret', validatorRules.appSecret]" placeholder="请输入app_secret"></a-input>
        </a-form-item>
        <a-form-item label="module" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'module', validatorRules.module]" placeholder="请输入module"></a-input>
        </a-form-item>
        <a-form-item label="type" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'type', validatorRules.type]" placeholder="请输入type"></a-input>
        </a-form-item>
        <a-form-item label="code" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'code', validatorRules.code]" placeholder="请输入code"></a-input>
        </a-form-item>
        <a-form-item label="有效期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="[ 'expire', validatorRules.expire]" :min="1" :max="300000000" />
        </a-form-item>
        
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  
  export default {
    name: "center_AppModal",
    components: {      
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"应用",
        width:800,
        visible: false, 
        model: {
        },
        cStateSwitch:true, //状态 默认启用
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
          signName:{rules: [{ required: true, message: '请输入短信签名!' }]},
          appKey:{rules: [{ required: true, message: '请输入app_key!' }]},
          appSecret:{rules: [{ required: true, message: '请输入app_secret!' }]},
          module:{rules: [{ required: true, message: '请输入module!' }]},
          type:{rules: [{ required: true, message: '请输入type!' }]},
          code:{rules: [{ required: true, message: '请输入code!' }]},
          expire:{rules: [{ required: true, message: '请输入expire!' }]}
        },
        url: {
          save: "/sys/config/sms/save"
        }  
      }
    },
    created () {
      

    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);      
        if(!record.hasOwnProperty("id")){
          this.model.expire = 300;
        }
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'signName','detail','appKey','appSecret','module','type','code','expire'))
        })
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
            let httpurl = this.url.save;
            let method = 'post';
            let formData = Object.assign(this.model, values);
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.msg);
                this.$emit('ok',formData);
              }else{
                that.$message.warning(res.msg);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
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
