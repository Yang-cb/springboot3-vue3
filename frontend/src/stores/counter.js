import {reactive} from 'vue'
import {defineStore} from 'pinia'

export const useCounterStore = defineStore('counter', () => {
    const auth = reactive({
        user: null
    })
    return {auth}
})
