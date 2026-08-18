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

        <a-form-item label="租户id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'id', {}]" placeholder="请输入租户id"></a-input>
        </a-form-item>          
        <a-form-item label="租户名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'name', validatorRules.name]" placeholder="请输入租户名称"></a-input>
        </a-form-item>
        <a-form-item label="logo" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'logo', {}]" placeholder="请输入租户logo"></a-input>
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
    name: "center_TenantModal",
    components: {      
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"租户",
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
          name:{rules: [{ required: true, message: '请输入服务名称!' }]}
        },
        saveType: '',
        url: {
          add: "/sys/center/tenant/add",
          update: "/sys/center/tenant/update"
        }  
      }
    },
    created () {
      

    },
    methods: {
      add () {        
        this.edit({saveType:'add'});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);      
        if(record.hasOwnProperty("id")){
          this.stateSwitch=record.status==1?true:false;
        }
        this.saveType='update';
        if(record.hasOwnProperty("saveType")){
          this.saveType=record.saveType;
        }
        console.log(this.saveType);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'id', 'name','logo'))
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
            let httpurl ='';
            if(this.saveType=='add'){
                httpurl =this.url.add;
            }else{
              httpurl =this.url.update;
            }
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
