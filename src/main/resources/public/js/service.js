var as = angular.module('AboutMeApp.services', []);

as.service('Session', function () {
    this.create = function (data) {
        this.nickName = data.nickName;
        this.userRoles = [];
        angular.forEach(data.authorities, function (value, key) {
            this.push(value.authority);
        }, this.userRoles);
    };

    this.invalidate = function () {
        this.nickName = null;
        this.userRoles = [];
    }
});

as.service('AuthService', function ($rootScope, $http, Session, $log, AUTH_EVENTS) {
    var authService = {};

    authService.loginConfirmed = function (response) {
        Session.create(response.content);
        authService.authenticated = true;
        $rootScope.$broadcast(AUTH_EVENTS.signInSuccess, {
            data: response
        });
    };

    authService.signUp = function (user) {
        return $http.post('api/auth/signUp', user).success(function (response) {
            $log.debug(response);
            authService.loginConfirmed(response);

            return response;
        });
    };

    authService.isAuthenticated = function () {
        return this.authenticated;
    };

    authService.signIn = function (user) {
        return $http.post('api/auth/signIn', user).success(function (profile) {
            $log.debug(profile);
            Session.create(profile);
            authService.authenticated = true;
            $rootScope.$broadcast(AUTH_EVENTS.signInSuccess, {
                data: profile
            });

            return profile;
        });
    };

    authService.signOut = function () {
        return $http.post('api/auth/signOut').success(function (response) {
            $log.debug(response);
            Session.invalidate();
            authService.authenticated = false;
            $rootScope.$broadcast(AUTH_EVENTS.signOutSuccess, {
                content: response
            });

            return response;
        });
    };

    authService.isAuthorized = function (authorizedRoles) {
        if (!angular.isArray(authorizedRoles)) {
            if (authorizedRoles === '*') {
                return true;
            }
            authorizedRoles = [authorizedRoles];
        }
        var isAuthorized = false;

        angular.forEach(authorizedRoles, function (authorizedRole) {
            if (isAuthorized) {
                return;
            }
            if (authorizedRole === '*') {
                isAuthorized = true;
            } else if (!authService.authenticated) {
                isAuthorized = false;
            } else {
                isAuthorized = Session.userRoles.indexOf(authorizedRole) !== -1;
            }
        });

        return isAuthorized;
    };

    authService.isAuthorizedRole = function (authorizedRoles) {
        var isAuthorized = false;

        angular.forEach(authorizedRoles, function (authorizedRole) {
            if (isAuthorized) {
                return;
            }
            if (authorizedRole === '*') {
                isAuthorized = true;
            } else if (!authService.authenticated) {
                isAuthorized = false;
            } else {
                isAuthorized = Session.userRoles.indexOf(authorizedRole) !== -1;
            }
        });
    };

    authService.getAccount = function () {
        $http.get('api/auth/account').success(function (profile) {
            $log.debug(profile);

            if (profile) {
                Session.create(profile);
                authService.authenticated = true;
                $rootScope.$broadcast(AUTH_EVENTS.signInSuccess, {
                    data: profile
                });
            }
        });
    };

    return authService;
});

as.service('LocationService', function ($location) {
    var locationService = {};

    locationService.location = '/';
    locationService.saveLocation = function (url, params) {
        if (!url || url.length === 0) {
            locationService.location = $location.path();
        } else {
            var resolvedUrl = url;

            for (var k in params) {
                resolvedUrl = resolvedUrl.replace(':' + k, params[k]);
            }
            locationService.location = resolvedUrl;
        }
    };

    locationService.gotoLast = function () {
        $location.path(locationService.location);
    };

    return locationService;
});

as.service('DataService', function () {
    var dataService = {};

    dataService.data = {};

    dataService.set = function (target, data) {
        dataService.data[target] = data;
    };

    dataService.get = function (target) {
        return dataService.data[target];
    };

    return dataService;
});

as.service('FileService', function ($q, $log) {
    var fileService = {};

    var onLoad = function (reader, deferred, scope) {
        return function () {
            scope.$apply(function () {
                deferred.resolve(reader.result);
            });
        };
    };

    var onError = function (reader, deferred, scope) {
        return function () {
            scope.$apply(function () {
                deferred.reject(reader.result);
            });
        };
    };

    var getReader = function (deferred, scope) {
        var reader = new FileReader();
        reader.onload = onLoad(reader, deferred, scope);
        reader.onerror = onError(reader, deferred, scope);

        return reader;
    };

    fileService.readAsDataURL = function (file, scope) {
        var deferred = $q.defer();

        var reader = getReader(deferred, scope);
        reader.readAsDataURL(file);

        return deferred.promise;
    };

    return fileService;
});

as.service('LikeService', function ($http) {
    var likeService = {};

    likeService.like = function (post) {
        return $http.post('api/like/post', post);
    };

    likeService.dislike = function (post) {
        return $http.post('api/dislike/post', post);
    };

    return likeService;
});

as.service('HtmlEncoder', function () {
    var HtmlToken = {
        WORD: 0,
        TAG: 1
    };
    var HtmlTokenizeState = {
        DEFAULT: 0,
        TAG_START: 1,
        TAG_END: 2,
        WORD_START: 3,
        WORD_END: 4
    };

    var HtmlLexer = {};

    HtmlLexer.tokenizeState = HtmlTokenizeState.DEFAULT;
    HtmlLexer.value = '';
    HtmlLexer.position = -1;
    HtmlLexer.expression = '';
    HtmlLexer.tokenize = function () {
        var lexems = [];
        var ch;

        while ((ch = HtmlLexer.getNext()) != -1) {
            var lexem = HtmlLexer.nextLexem(ch);

            if (lexem !== null) {
                lexems.push(lexem);
            }
        }

        return lexems;
    };
    HtmlLexer.getNext = function () {
        HtmlLexer.position += 1;
        return HtmlLexer.position < HtmlLexer.expression.length ?
            HtmlLexer.expression.charAt(HtmlLexer.position) : -1;
    };
    HtmlLexer.nextLexem = function (ch) {
        switch (HtmlLexer.tokenizeState) {
            case HtmlTokenizeState.DEFAULT:
                if (ch == '<') {
                    HtmlLexer.tokenizeState = HtmlTokenizeState.TAG_START;
                    HtmlLexer.position -= 1;
                } else {
                    HtmlLexer.tokenizeState = HtmlTokenizeState.WORD_START;
                    HtmlLexer.position -= 1;
                }
                break;
            case HtmlTokenizeState.TAG_START:
                HtmlLexer.value += ch;
                if (ch == '>') {
                    HtmlLexer.tokenizeState = HtmlTokenizeState.TAG_END;
                    HtmlLexer.position -= 1;
                }
                break;
            case HtmlTokenizeState.TAG_END:
                return HtmlLexer.createToken(HtmlToken.TAG, false);
            case HtmlTokenizeState.WORD_START:
                if (ch == '<') {
                    HtmlLexer.tokenizeState = HtmlTokenizeState.WORD_END;
                    HtmlLexer.position -= 1;
                } else {
                    HtmlLexer.value += ch;
                }
                break;
            case HtmlTokenizeState.WORD_END:
                return HtmlLexer.createToken(HtmlToken.WORD, true);
        }

        return null;
    };
    HtmlLexer.createToken = function (token, back) {
        var lexem = {
            token: token,
            value: HtmlLexer.value
        };

        HtmlLexer.value = '';

        HtmlLexer.tokenizeState = HtmlTokenizeState.DEFAULT;
        if (back) {
            HtmlLexer.position -= 1;
        }

        return lexem;
    };

    var HtmlEncoder = {};

    HtmlEncoder.encode = function (html) {
        HtmlLexer.expression = html;

        var lexems = HtmlLexer.tokenize();
        var encoded = '';

        lexems.forEach(function(lexem) {
            switch (lexem.token) {
                case HtmlToken.WORD:
                    encoded += lexem.value;
                    break;
                case HtmlToken.TAG:
                    if (lexem.value.startsWith("<code")) {
                        encoded += '<pre>';
                        encoded += lexem.value.substring(0, 5) + ' hljs' + lexem.value.substring(5);
                    } else if (lexem.value === '</code>') {
                        encoded += lexem.value;
                        encoded += '</pre>';
                    } else {
                        encoded += lexem.value.replace("<", "&lt;").replace(">", "&gt;");
                    }
                    break;
            }
        });

        return encoded;
    };

    return HtmlEncoder;
});