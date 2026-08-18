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
          
        <a-form-item label="服务名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'name', validatorRules.name]" placeholder="请输入服务名称"></a-input>
        </a-form-item>
        <a-form-item label="服务名" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'service', validatorRules.service]" placeholder="请输入服务名"></a-input>
        </a-form-item>

        <a-form-item label="服务路径" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'path', validatorRules.path]" placeholder="请输入服务路径"></a-input>
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
    name: "center_ServiceModal",
    components: {      
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"服务",
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
          name:{rules: [{ required: true, message: '请输入服务名称!' }]},
          service:{rules: [{ required: true, message: '请输入服务名!' }]},
          path:{rules: [{ required: true, message: '请输入服务路径!' }]}
        },
        url: {
          save: "/sys/center/service/save"
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
          this.form.setFieldsValue(pick(this.model,'name','service','path'))
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
            formData.status=0;
            if(this.stateSwitch==true){
              formData.status=1;
            }
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
