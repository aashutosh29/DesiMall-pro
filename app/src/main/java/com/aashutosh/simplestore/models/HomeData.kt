package com.aashutosh.simplestore.models

import com.aashutosh.simplestore.models.category.CategoryResponse
import com.aashutosh.simplestore.models.product.Products

data class HomeData(
    val products: Products,
    val categoryResponse: CategoryResponse,
    val fireStoreImage: List<String>
) : List<Any> {
    override val size: Int
        get() = TODO("Not yet implemented")

    override fun contains(element: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<Any>): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns the element at the specified index in the list.
     */
    override fun get(index: Int): Any {
        TODO("Not yet implemented")
    }

    /**
     * Returns the index of the first occurrence of the specified element in the list, or -1 if the specified
     * element is not contained in the list.
     */
    override fun indexOf(element: Any): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun iterator(): Iterator<Any> {
        TODO("Not yet implemented")
    }

    /**
     * Returns the index of the last occurrence of the specified element in the list, or -1 if the specified
     * element is not contained in the list.
     */
    override fun lastIndexOf(element: Any): Int {
        TODO("Not yet implemented")
    }

    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     */
    override fun listIterator(): ListIterator<Any> {
        TODO("Not yet implemented")
    }

    /**
     * Returns a list iterator over the elements in this list (in proper sequence), starting at the specified [index].
     */
    override fun listIterator(index: Int): ListIterator<Any> {
        TODO("Not yet implemented")
    }

    /**
     * Returns a view of the portion of this list between the specified [fromIndex] (inclusive) and [toIndex] (exclusive).
     * The returned list is backed by this list, so non-structural changes in the returned list are reflected in this list, and vice-versa.
     *
     * Structural changes in the base list make the behavior of the view undefined.
     */
    override fun subList(fromIndex: Int, toIndex: Int): List<Any> {
        TODO("Not yet implemented")
    }
}

