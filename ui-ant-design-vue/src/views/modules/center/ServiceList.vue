<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" :form="form" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="服务名称">
              <a-input placeholder="请输入服务名称" v-model="queryParam.name"></a-input>
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
      <a-button @click="handleAdd" type="primary" icon="plus">添加服务</a-button>      
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
        </span>        

        <!-- 状态渲染模板 -->
        <template slot="customRenderState" slot-scope="status">
          <a-tag v-if="status==0" color="#dd4b39">禁用</a-tag>
          <a-tag v-if="status==1" color="#3c8dbc">正常</a-tag>          
        </template>

      </a-table>
    </div>
    <!-- table区域-end -->
    <app-modal ref="modalForm" @ok="modalFormOk"></app-modal>
  </a-card>
</template>

<script>
  import AppModal from './modules/ServiceModal'
  import {FirefoxListMixin} from '@/mixins/FirefoxListMixin'

  export default {
    name: "center_ServiceList",
    mixins: [FirefoxListMixin],
    components: {
      AppModal
    },
    data() {      
      return {
        description: '服务管理',
        queryParam: {},
        columns: [
          {
            title:'名称',
            align:"left",
            dataIndex: 'name'
          },
          {
            title:'服务名',
            align:"left",
            dataIndex: 'service'
          },
          {
            title:'路径',
            align:"left",
            dataIndex: 'path'
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
          list: "/sys/center/service/page"
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