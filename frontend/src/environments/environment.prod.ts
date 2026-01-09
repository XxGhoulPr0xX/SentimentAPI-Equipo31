export const environment = {
    production: true,
    apiUrl: '/api'
};
// Note: '/api' relative path allows us to use Nginx proxy pass without hardcoding the IP/Domain in the build.
// This is much easier for hackathons than rebuilding every time the IP changes.
