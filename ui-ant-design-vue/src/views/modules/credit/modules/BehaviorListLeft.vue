<template>
  <a-card :loading="cardLoading" :bordered="false" style="height: 100%;">
    <a-spin :spinning="loading">
      <!--<a-input-search @search="handleSearch" style="width:100%;margin-top: 10px" placeholder="输入机构名称查询..." enterButton />-->
      <a-tree
        showLine
        checkStrictly
        :expandedKeys.sync="expandedKeys"
        :selectedKeys="selectedKeys"
        :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
        :treeData="treeDataSource"
        @select="handleTreeSelect"
      />
    </a-spin>
  </a-card>
</template>

<script>
  import { querycreditTypeTreeList } from '@/api/api'

  export default {
    name: 'credit_BehaviorListLeft',
    props: ['value'],
    data() {
      return {
        cardLoading: true,
        loading: false,
        treeDataSource: [],
        selectedKeys: [],
        expandedKeys: []
      }
    },
    created() {
      this.queryTreeData()
    },
    methods: {
      queryTreeData(keyword) {
        this.commonRequestThen(querycreditTypeTreeList({
          key: keyword ? keyword : undefined
        }))
      },
      handleSearch(value) {
        if (value) {
          this.commonRequestThen(searchByKeywords({ keyWord: value }))
        } else {
          this.queryTreeData()
        }
      },
      handleTreeSelect(selectedKeys, event) {
        if (selectedKeys.length > 0 && this.selectedKeys[0] !== selectedKeys[0]) {
          this.selectedKeys = [selectedKeys[0]]
          let typeId = event.node.dataRef.id
          this.emitInput(typeId)
        }
      },
      emitInput(typeId) {
        this.$emit('input', typeId)
      },
      commonRequestThen(promise) {
        this.loading = true
        promise.then(res => {
          if (res.success) {
            this.treeDataSource = res.data
            // 默认选中第一条数据、默认展开所有第一级
            if (res.data.length > 0) {
              this.expandedKeys = []
              res.data.forEach((item, index) => {
                if (index === 0) {
                  this.emitInput(item.id)
                }
                this.expandedKeys.push(item.id)
              })
            }
          } else {
            this.$message.warn(res.message)
            console.error('类型查询失败:', res)
          }
        }).finally(() => {
          this.loading = false
          this.cardLoading = false
        })
      },

    }
  }
</script>

<style scoped>

</style>