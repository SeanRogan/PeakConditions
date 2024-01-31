import axios from 'axios';



const baseURL = process.env.NEXT_PUBLIC_SERVER_HOSTNAME || 'http://localhost:8080';
export const axiosInstance = axios.create({
    baseURL: `${baseURL}/api/v1`,
    withCredentials: true,
    responseType: "json",
    headers: {'Content-Type': 'application/json'}
});

axiosInstance.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        // Any status codes that falls outside the range of 2xx cause this function to trigger
        const originalRequest = error.config;

        // Check if the status is 403
        if (error.response.status === 403 && !originalRequest._retry) {

            originalRequest._retry = true; // mark it so that we don't get into an infinite loop

            // Attempt to get a new token
            return axiosInstance.post('/auth/refresh')
                .then(res => {
                    if (res.status === 200) {
                        // If refresh was successful
                        // Return the original request with the new token
                        return axiosInstance(originalRequest);
                    }
                });
        }

        // If it's not a 403, or if refreshing fails, reject the promise
        return Promise.reject(error);
    }
);

