package com.thecoppermind.recyclerView.refactor


interface RecyclerItemViewInterface {

    //    @get:LayoutRes
    val itemViewType: Int

//    val layoutId: Int
//        get() = itemViewType

//    @Suppress("UNCHECKED_CAST") // TODO подумать
//    private fun castHolder(holder : RecyclerViewHolder) : T {
//        return holder as T
//    }

//    fun bindByAdapter(holder: RecyclerViewHolder) {
//        bind(castHolder(holder))
//    }

    fun bind(holder: RecyclerHolder)
    fun bindWithPayload(holder: RecyclerHolder, payloads: List<Any>) = bind(holder)

    /**
     * Проверить совпадает ли данный элемент с указанным
     * BestPractise - сравнивать по id
     * @param newItem
     * *
     * @return true, если это тот же элемент, и его вью можно переиспользовать
     */
    fun areItemsTheSame(newItem: RecyclerItemViewInterface): Boolean = itemId == newItem.itemId

    /**
     * Если контент конкретного элемента может меняться, то в данном методе необходимо проверить изменился ли он
     * BestPractise - сравнивать только по изменяемым полям (в крйане случае - по equals())
     * @param newItem
     * *
     * @return true если требуется вызов метода bindWithPayload() для данного элемента
     */
    //    fun areContentsTheSame(newItem: RecyclerItemViewInterface): Boolean = this == newItem
    fun areContentsTheSame(newItem: RecyclerItemViewInterface): Boolean {
        return dataToCompare.indices.none { dataToCompare[it] != newItem.dataToCompare[it] }
        // TODO понять как работают последовательности и почему некс код выдаёт валидный результат?
//        val none =
//            dataToCompare
//                    .asSequence()
//                    .filterIndexed {index, value -> value != newItem.dataToCompare[index] }
//                    .none()
//        return none
    }

    /**
     * Получение типа обвновления контента от старого отображения элемента к новому
     * @param newItem
     * *
     * @return
     */
    fun getPayload(newItem: RecyclerItemViewInterface): Any = DEFAULT_PAYLOAD

    /**
     * У каждого элемента должен быть уникальный идентификатор, по которому мы можем определить его переиспользование, или найти в списке элементов для замены
     * Если идентификатор не зада явно - элемент считается уникальным для данного списка
     */
    val itemId: Any
        get() = UNIQUE
//    fun getItemId() : Any = UNIQUE

    /**
     * Список полей, по которым мы будем сравнивать элементы на предмет "одинаковости" для функции areContentsTheSame
     * Если не будет задано ни одного поля для сравнения - контент элемента считается неизменным
     */
    val dataToCompare: List<Any>
        get() = ArrayList()

    companion object {

        // -------------- Payloads ----------------------------
        protected const val DEFAULT_PAYLOAD = "default_payload"

        // -------------- Default itemId for unique items ----------------------------
        private const val UNIQUE: String = "Unique"
    }
}


//
//interface RecyclerItemViewInterface<in T: RecyclerViewHolder>{
//
//    //    @get:LayoutRes
//    val itemViewType: Int
//
//    val layoutId: Int
//        get() = itemViewType
//
//    @Suppress("UNCHECKED_CAST") // TODO подумать
//    private fun castHolder(holder : RecyclerViewHolder) : T {
//        return holder as T
//    }
//
//    fun bindByAdapter(holder: RecyclerViewHolder) {
//        bind(castHolder(holder))
//    }
//
//    fun bind(holder: T)
//
//    fun bindWithPayload(holder: T, payloads: List<Any>) = bind(holder)
//
//    /**
//     * Проверить совпадает ли данный элемент с указанным
//     * BestPractise - сравнивать по id
//     * @param newItem
//     * *
//     * @return true, если это тот же элемент, и его вью можно переиспользовать
//     */
//    fun areItemsTheSame(newItem: RecyclerItemViewInterface<RecyclerViewHolder>): Boolean = itemId == newItem.itemId
//
//    /**
//     * Если контент конкретного элемента может меняться, то в данном методе необходимо проверить изменился ли он
//     * BestPractise - сравнивать только по изменяемым полям (в крйане случае - по equals())
//     * @param newItem
//     * *
//     * @return true если требуется вызов метода bindWithPayload() для данного элемента
//     */
//    fun areContentsTheSame(newItem: RecyclerItemViewInterface<RecyclerViewHolder>): Boolean = this == newItem
//
//    /**
//     * Получение типа обвновления контента от старого отображения элемента к новому
//     * @param newItem
//     * *
//     * @return
//     */
//    fun getPayload(newItem: RecyclerItemViewInterface<RecyclerViewHolder>) = DEFAULT_PAYLOAD
//
//    /**
//     * У каждого элемента должен быть уникальный идентификатор, по которому мы можем определить его переиспользование, или найти в списке элементов для замены
//     * @return
//     */
//    val itemId : Any
//        get() = UNIQUE
////    fun getItemId() : Any = UNIQUE
//
//    companion object {
//
//        // -------------- Payloads ----------------------------
//        protected const val DEFAULT_PAYLOAD = "default_payload"
//
//        // -------------- Default itemId for unique items ----------------------------
//        private const val UNIQUE: String = "Unique"
//    }
//}