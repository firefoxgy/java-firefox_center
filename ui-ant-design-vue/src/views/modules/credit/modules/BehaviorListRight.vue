<template>
  <a-card class="j-address-list-right-card-box" :loading="cardLoading" :bordered="false">

    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="10">

          <a-col :md="6" :sm="12">
            <a-form-item label="行为名称" style="margin-left:8px">
              <a-input placeholder="请输入行为名称查询" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>

          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="6" :sm="24">
              <a-button type="primary" @click="searchQuery" icon="search" style="margin-left: 18px">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button type="primary" @click="handleAddReocrd" icon="plus" style="margin-left: 8px" v-show="addShow">新增</a-button>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>

    <a-table
      ref="table"
      size="middle"
      rowKey="id"
      bordered
      :pagination="ipagination"
      :columns="columns"
      :dataSource="dataSource"
      :loading="loading"
      @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <!-- <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record)">
            <a>删除</a>
          </a-popconfirm>
          <a-divider type="vertical" /> -->
          <!--<a @click="handleAddSub(record)">添加下级</a>-->
        </span>
    </a-table>

  <behavior-modal ref="modalForm" @ok="modalFormOk"></behavior-modal>
  </a-card>
</template>

<script>
  import { getAction,deleteAction } from '@/api/manage'
  import { FirefoxListMixin } from '@/mixins/FirefoxListMixin'
  import BehaviorModal from './BehaviorModal'

  export default {
    name: 'credit_BehaviorListRight',
    mixins: [FirefoxListMixin],
    components: {
      BehaviorModal
    },
    props: ['value'],
    data() {
      return {
        description: '行为信息',
        cardLoading: true,
        addShow: false,
        columns: [
          {
            title: '分类名称',
            width: '15%',
            align: 'center',
            dataIndex: 'typeName'
          }, 
          {
            title: '行为编号',
            width: '25%',
            align: 'center',
            dataIndex: 'no'
          },
          {
            title: '行为名称',
            width: '15%',
            align: 'center',
            dataIndex: 'name'
          },
          {
            title: '行为分值',
            align: 'center',
            dataIndex: 'num'
          },
          {
            title: '行为描述',
            width: '20%',
            align: 'center',
            dataIndex: 'detail'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/sys/credit/behavior/page"
        }
      }
    },
    watch: {
      value: {
        immediate: true,
        handler(typeId) {
          this.addShow=true
          this.dataSource = []
          this.loadData(1, typeId)
        }
      },
    },
    created() {
    },
    methods: {
      loadData(pageNum, typeId) {
        this.loading = true
        if (pageNum === 1) {
            this.ipagination.current = 1
        }
        if (!typeId) {
            getAction(this.url.list, {
                ...this.getQueryParams()
            }).then((res) => {
                if (res.success) {
                    this.dataSource = res.data.records
                    this.ipagination.total = res.data.total
                }
            }).finally(() => {
                this.loading = false
                this.cardLoading = false
            })
          // update-end- --- author:wangshuai ------ date:20200102 ---- for:传过来的部门编码为空全查
        }else{
        //加载数据 若传入参数1则加载第一页的内容
        getAction(this.url.list, {
          typeId,
          ...this.getQueryParams()
        }).then((res) => {
          if (res.success) {
            this.dataSource = res.data.records
            this.ipagination.total = res.data.total
          }
        }).finally(() => {
          this.loading = false
          this.cardLoading = false
        })
        }
      },

      searchQuery() {
        this.loadData(1, this.value)
      },
      searchReset() {
        this.queryParam = {}
        this.loadData(1, this.value)
      },
      handleAddReocrd(){
        this.$refs.modalForm.title = "添加行为";
        this.$refs.modalForm.edit({'typeId':this.value});
        this.$refs.modalForm.disableSubmit = false;
      },
      handleTableChange(pagination, filters, sorter) {
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field
          this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc'
        }
        this.ipagination = pagination
        this.loadData(null, this.value)
      },
      handleDelete: function (record) {
        let that = this;
        deleteAction(that.url.delete, {id: record.id}).then((res) => {
          if (res.success) {
              that.$message.success(res.message);
              that.addOk()
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      modalFormOk(formData,arr){
        if(!formData.id){
          this.addOk()
        }else{
          this.editOk(formData,this.dataSource)
          this.dataSource=[...this.dataSource]
        }        
      },
      addOk(){
        this.loadData(1, this.value)
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
<style>
  .j-address-list-right-card-box .ant-table-placeholder {
    min-height: 46px;
  }
</style>
<style scoped>
  .j-address-list-right-card-box {
    height: 100%;
    min-height: 300px;
  }
</style>