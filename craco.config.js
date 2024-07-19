// craco.config.js
module.exports = {
    webpack: {
      configure: (webpackConfig) => {
        webpackConfig.ignoreWarnings = [
          (warning) =>
            warning.module &&
            warning.module.resource.includes('@react-keycloak') &&
            warning.details &&
            warning.details.includes('source map'),
        ];
  
        webpackConfig.module.rules.push({
          test: /\.js$/,
          enforce: 'pre',
          use: ['source-map-loader'],
          exclude: /node_modules\/@react-keycloak/,
        });
        return webpackConfig;
      },
    },
  };
  