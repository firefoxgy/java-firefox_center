<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" :form="form" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="4">
            <a-form-item label="appId">
              <a-input placeholder="appId" v-model="queryParam.appId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="4">
            <a-form-item label="tenantId">
              <a-input placeholder="tenantId" v-model="queryParam.tenantId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="4">
            <a-form-item label="手机号">
              <a-input placeholder="手机号" v-model="queryParam.phone"></a-input>
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
      <a-button @click="handleAdd" type="primary" icon="plus">添加用户</a-button>
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
          <a @click="handleView(record)" >详情</a>
        </span>

        <!-- 状态渲染模板 -->
        <template slot="customRenderSource" slot-scope="source">
          <span v-if="source==0" >迁移</span>
          <span v-if="source==1" >注册</span>   
        </template>

        <template slot="customRenderState" slot-scope="status">
          <a-tag v-if="status==0" color="#dd4b39">禁用</a-tag>
          <a-tag v-if="status==1" color="#3c8dbc">正常</a-tag>          
        </template>

      </a-table>
    </div>
    <!-- table区域-end -->
    <app-user-modal ref="modalForm" @ok="modalFormOk"></app-user-modal>
    <app-user-detail ref="modalDetailForm" @ok="modalFormOk"></app-user-detail>
  </a-card>
</template>

<script>
  import AppUserModal from './modules/AppUserModal'
  import AppUserDetail from './modules/AppUserDetail'
  import {FirefoxListMixin} from '@/mixins/FirefoxListMixin'

  export default {
    name: "user_AppUserList",
    mixins: [FirefoxListMixin],
    components: {
      AppUserModal,
      AppUserDetail
    },
    data() {      
      return {
        description: 'App用户管理',
        queryParam: {},
        columns: [
          {
            title:'手机号',
            align:"left",
            dataIndex: 'phone'
          },
          {
            title:'open_int_id',
            align:"left",
            dataIndex: 'openIntId'
          },
          {
            title:'open_id',
            align:"left",
            dataIndex: 'openId'
          },
          {
            title:'应用id',
            align:"left",
            dataIndex: 'appId'
          },
          {
            title:'租户id',
            align:"left",
            dataIndex: 'tenantId'
          },
          {
            title:'uid',
            align:"left",
            dataIndex: 'uid'
          },
          {
            title:'用户名',
            align:"left",
            dataIndex: 'username'
          },
          {
            title:'昵称',
            align:"left",
            dataIndex: 'nickname'
          },
          {
            title:'注册来源',
            align:"left",
            dataIndex: 'regFrom'
          },
          {
            title:'用户来源',
            align:"left",
            dataIndex: 'source',
            scopedSlots: { customRender: 'customRenderSource' },
            filterMultiple: false,
            filters: [
              { text: '注册', value: 1 },
              { text: '迁移', value: 0 },
            ]
          },
          {
            title: '状态',
            align: "center",
            dataIndex: 'status',
            scopedSlots: { customRender: 'customRenderState' },
            filterMultiple: false,
            filters: [
              { text: '正常', value: 1 },
              { text: '禁用', value: 0 },
            ]
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
          list: "/sys/user/app/page"
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
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>