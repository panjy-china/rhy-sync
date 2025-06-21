import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore(
  "user",
  () => {
    const token = ref('')
    function setToken(params) {
      token.value = params
    }
    return { token, setToken }
  },
  {
    persist: true
  });