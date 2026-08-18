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

        <a-form-item label="app_id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'appId', validatorRules.appId]" placeholder="请输入app_id"></a-input>
        </a-form-item>
        <a-form-item label="tenant_id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'tenantId', validatorRules.tenantId]" placeholder="请输入tenant_id"></a-input>
        </a-form-item>          
        <a-form-item label="用户名" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'username', validatorRules.username]" placeholder="请输入用户名"></a-input>
        </a-form-item>
        <a-form-item label="手机号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'phone', validatorRules.phone]" placeholder="请输入手机号"></a-input>
        </a-form-item>
        <a-form-item label="email" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'email', {}]" placeholder="请输入email"></a-input>
        </a-form-item>
        <a-form-item  v-if="!model.hasOwnProperty('id')" label="密码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'password', validatorRules.password]" placeholder="请输入密码"></a-input>
        </a-form-item>
        <a-form-item label="昵称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'nickname', {}]" placeholder="请输入昵称"></a-input>
        </a-form-item>
        <a-form-item label="头像" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'headerImg', {}]" placeholder="请输入头像"></a-input>
        </a-form-item>        
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
    name: "user_AppUserModal",
    components: {      
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"用户",
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
          appId:{rules: [{ required: true, message: '请输入app_id!' }]},
          tenantId:{rules: [{ required: true, message: '请输入tenant_id!' }]},
          username:{rules: [{ required: true, message: '请输入用户名!' }]},
          phone:{rules: [{ required: true, pattern: '^1(3|4|5|7|8)\\d{9}$', message: '请输入手机号!' }]},
          password:{rules: [{ required: true, message: '请输入密码!' }]}
        },
        url: {
          save: "/sys/user/app/save"
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
        if(record.hasOwnProperty("id")){
          this.stateSwitch=record.status==1?true:false;
        }
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'username','phone','email','password','nickname','headerImg'))
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
