function fn() {
  var env = karate.env || 'local';
  karate.log('karate.env system property was:', env);

  var config = {
    env: env,
    baseUrl: 'http://localhost:8080/api/v1/recipe-management',
    authToken: 'Bearer test-jwt-token'
  };

  if (env === 'local') {
    config.baseUrl = 'http://localhost:8080/api/v1/recipe-management';
  } else if (env === 'minikube') {
    config.baseUrl = 'http://sous-chef-proxy.local/api/v1/recipe-management';
  } else if (env === 'test') {
    config.baseUrl = 'http://localhost:8080/api/v1/recipe-management';
  }

  // Configure default headers for all requests
  karate.configure('headers', {
    'Authorization': config.authToken,
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  });

  karate.log('Final config:', config);
  return config;
}
