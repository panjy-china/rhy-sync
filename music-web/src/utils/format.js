import { dayjs } from 'element-plus'
// 时间格式化-处理表格
export const formatDate = (row, column, cellValue) => {
    // return new Date(cellValue).toLocaleDateString()
    // return dayjs(cellValue).format('YYYY年MM月DD日 HH:mm:ss')
    return dayjs(cellValue).format('YYYY-MM-DD')
}
export const formatDate2 = (date) => {
   return dayjs(date).format('YYYY-MM-DD')
}

export const formatDate3 = (date) => {
   return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}