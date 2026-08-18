<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" :form="form" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="应用名称">
              <a-input placeholder="请输入应用名称" v-model="queryParam.title"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
      <a-button @click="handleAdd" type="primary" icon="plus">添加应用</a-button>      
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"        
        size="middle"
        rowKey="id"
        bordered
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)" >编辑</a>
          <a-divider type="vertical" />
          <a @click="handleView(record)" >详情</a>
          <a-divider type="vertical" />
          <a @click="handleAppTenant(record)" >租户</a>
          <a-divider type="vertical" />
          <a @click="handleAppSms(record)" >短信</a>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->
    <app-modal ref="modalForm" @ok="modalFormOk"></app-modal>
    <app-detail ref="modalDetailForm"></app-detail>
    <app-tenant-modal ref="modalAppTenant"></app-tenant-modal>
    <app-sms-modal ref="modalAppSms"></app-sms-modal>
  </a-card>
</template>

<script>
  import AppModal from './modules/AppModal'
  import AppDetail from './modules/AppDetail'  
  import AppTenantModal from './modules/AppTenantModal'
  import AppSmsModal from './modules/AppSmsModal'  
  import {FirefoxListMixin} from '@/mixins/FirefoxListMixin'

  export default {
    name: "center_AppList",
    mixins: [FirefoxListMixin],
    components: {
      AppModal,
      AppDetail,
      AppTenantModal,
      AppSmsModal
    },
    data() {      
      return {
        description: '应用管理',
        queryParam: {},
        columns: [
          {
            title:'应用名称',
            align:"left",
            dataIndex: 'title'
          },
          {
            title:'client_id',
            align:"left",
            dataIndex: 'clientId'
          },
          {
            title:'token有效期(s)',
            align:"left",
            dataIndex: 'accessTokenValidity'
          },
          {
            title:'创建时间',
            align:"left",
            dataIndex: 'createTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: {customRender: 'action'},
            align: "center"
          }

        ],
        form: this.$form.createForm(this), 
        url: {
          list: "/sys/center/app/page"
        },
      }
    },
    computed: {

    },
    created(){

    },
    methods: {
      modalFormOk(formData,arr){
        this.loadData()     
      },      
      addOk(){
        this.loadData(1)
      },
      editOk(formData,arr){
        if(arr && arr.length>0){
          for(let i=0;i<arr.length;i++){
            if(arr[i].id==formData.id){
              arr[i]=formData
              break
            }else{
              this.editOk(formData,arr[i].children)
            }
          }
        }
      },
      handleAppTenant(record){
        this.$refs.modalAppTenant.title=record.title+'的租户';
        this.$refs.modalAppTenant.show(record.clientId);
      },
      handleAppSms(record){
        this.$refs.modalAppSms.title=record.title+'的短信模板';
        this.$refs.modalAppSms.show(record.clientId);
      }      
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>