<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" :form="form" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="短信签名">
              <a-input placeholder="请输入短信签名" v-model="queryParam.signName"></a-input>
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
      <a-button @click="handleAdd" type="primary" icon="plus">添加短信模板</a-button>      
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
      </a-table>
    </div>
    <!-- table区域-end -->
    <sms-modal ref="modalForm" @ok="modalFormOk"></sms-modal>
  </a-card>
</template>

<script>
  import SmsModal from './modules/SmsModal'
  import {FirefoxListMixin} from '@/mixins/FirefoxListMixin'

  export default {
    name: "center_SmsList",
    mixins: [FirefoxListMixin],
    components: {
      SmsModal
    },
    data() {      
      return {
        description: '短信模板管理',
        queryParam: {},
        columns: [
          {
            title:'短信签名',
            align:"left",
            dataIndex: 'signName'
          },
          {
            title:'code',
            align:"left",
            dataIndex: 'code'
          },
          {
            title:'app_key',
            align:"left",
            dataIndex: 'appKey'
          },
          {
            title:'app_secret',
            align:"left",
            dataIndex: 'appSecret'
          },
          {
            title:'module',
            align:"left",
            dataIndex: 'module'
          },
          {
            title:'type',
            align:"left",
            dataIndex: 'type'
          },
          {
            title:'过期时间',
            align:"left",
            dataIndex: 'expire'
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
          list: "/sys/config/sms/page"
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