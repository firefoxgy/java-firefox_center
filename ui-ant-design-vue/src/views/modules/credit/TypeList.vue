<template>
  <a-card :bordered="false">
    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
      <a-button @click="handleAdd" type="primary" icon="plus">添加分类</a-button>      
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
    <type-modal ref="modalForm" @ok="modalFormOk"></type-modal>
  </a-card>
</template>

<script>
  import TypeModal from './modules/TypeModal' 
  import {FirefoxListMixin} from '@/mixins/FirefoxListMixin'

  export default {
    name: "credit_TypeList",
    mixins: [FirefoxListMixin],
    components: {
      TypeModal
    },
    data() {      
      return {
        description: '分类管理',
        queryParam: {},
        columns: [
          {
            title:'编号',
            align:"left",
            dataIndex: 'no'
          },
          {
            title:'名称',
            align:"left",
            dataIndex: 'name'
          },
          {
            title:'详情',
            align:"left",
            dataIndex: 'detail'
          },
          {
            title:'创建时间',
            align:"left",
            dataIndex: 'createTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }

        ],
        form: this.$form.createForm(this), 
        url: {
          list: "/sys/credit/type/page"
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