import axios from 'axios';

const API_GATEWAY_URL = 'http://localhost:9001';

// Function to get all products
export const getAllProducts = async () => {
    try {
        const response = await axios.get(`${API_GATEWAY_URL}/api/product`);
        return response.data;
    } catch (error) {
        console.error("Error fetching products:", error);
        throw error;
    }
};

// Function to create a product
export const createProduct = async (productRequest) => {
    try {
        const response = await axios.post(`${API_GATEWAY_URL}/api/product`, productRequest);
        return response.data;
    } catch (error) {
        console.error("Error creating product:", error);
        throw error;
    }
};

// Function to update a product
export const updateProduct = async (id, productRequest) => {
    try {
        const response = await axios.put(`${API_GATEWAY_URL}/api/product/${id}`, productRequest);
        return response.data;
    } catch (error) {
        console.error("Error updating product:", error);
        throw error;
    }
};

// Function to delete a product
export const deleteProduct = async (id) => {
    try {
        const response = await axios.delete(`${API_GATEWAY_URL}/api/product/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error deleting product:", error);
        throw error;
    }
};

// Function to add an item to the basket
export const addToBasket = async (isbn, quantity) => {
    try {
        const response = await axios.post(`${API_GATEWAY_URL}/api/baskets/add`, { isbn, quantity });
        return response.data;
    } catch (error) {
        console.error("Error adding to basket:", error);
        throw new Error('Error adding to basket: ' + error.message);
    }
};

// Function to remove an item from the basket
export const removeFromBasket = async (isbn) => {
    try {
        const response = await axios.post(`${API_GATEWAY_URL}/api/basket/remove`, { isbn });
        return response.data;
    } catch (error) {
        console.error("Error removing from basket:", error);
        throw new Error('Error removing from basket: ' + error.message);
    }
};

// Function to update the quantity of an item in the basket
export const updateBasketItemQuantity = async (isbn, quantity) => {
    try {
        const response = await axios.put(`${API_GATEWAY_URL}/api/basket/update`, { isbn, quantity });
        return response.data;
    } catch (error) {
        console.error("Error updating basket item quantity:", error);
        throw new Error('Error updating basket item quantity: ' + error.message);
    }
};

// Function to get cart items
export const getBasketItems = async () => {
    try {
        const response = await axios.get(`${API_GATEWAY_URL}/api/baskets`);
        return response.data;
    } catch (error) {
        console.error("Error fetching basket items:", error);
        throw error;
    }
};
