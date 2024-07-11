import axios from 'axios';

const API_GATEWAY_URL = 'http://localhost:9000';

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
        const response = await axios.post(`${API_GATEWAY_URL}/products`, productRequest);
        return response.data;
    } catch (error) {
        console.error("Error creating product:", error);
        throw error;
    }
};

// Function to update a product
export const updateProduct = async (id, productRequest) => {
    try {
        const response = await axios.put(`${API_GATEWAY_URL}/products/${id}`, productRequest);
        return response.data;
    } catch (error) {
        console.error("Error updating product:", error);
        throw error;
    }
};

// Function to delete a product
export const deleteProduct = async (id) => {
    try {
        const response = await axios.delete(`${API_GATEWAY_URL}/products/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error deleting product:", error);
        throw error;
    }
};
