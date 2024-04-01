import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "@/plugins/axios";
import "@/access/index";
import "bytemd/dist/index.css";

//ArcoDesign
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";

createApp(App).use(ArcoVue).use(store).use(router).mount("#app");
