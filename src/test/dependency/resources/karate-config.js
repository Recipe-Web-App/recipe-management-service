function fn() {
  var env = karate.env || 'local';
  karate.log('karate.env system property was:', env);

  var config = {
    env: env,
    baseUrl: 'http://localhost:8080/api/v1/recipe-manager',
    authToken: 'Bearer test-jwt-token'
  };

  if (env === 'local') {
    config.baseUrl = 'http://localhost:8080/api/v1/recipe-manager';
  } else if (env === 'minikube') {
    config.baseUrl = 'http://recipe-manager.local/api/v1/recipe-manager';
  } else if (env === 'test') {
    config.baseUrl = 'http://localhost:8080/api/v1/recipe-manager';
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
