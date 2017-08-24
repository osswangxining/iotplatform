/*
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*@ngInject*/
export default function ManageAssetCredentialsController(assetService, $scope, $mdDialog, assetId, isReadOnly) {

    var vm = this;

    vm.credentialsTypes = [
        {
            name: 'Access token',
            value: 'ACCESS_TOKEN'
        }
    ];

    vm.assetCredentials = {};
    vm.isReadOnly = isReadOnly;

    vm.valid = valid;
    vm.cancel = cancel;
    vm.save = save;
    vm.clear = clear;

    loadAssetCredentials();

    function loadAssetCredentials() {
        assetService.getAssetCredentials(assetId).then(function success(assetCredentials) {
            vm.assetCredentials = assetCredentials;
        });
    }

    function cancel() {
        $mdDialog.cancel();
    }

    function valid() {
        return vm.assetCredentials &&
               (vm.assetCredentials.credentialsType === 'ACCESS_TOKEN'
                   && vm.assetCredentials.credentialsId
                   && vm.assetCredentials.credentialsId.length > 0
                   );
    }

    function clear() {
        vm.assetCredentials.credentialsId = null;
        vm.assetCredentials.credentialsValue = null;
    }

    function save() {
        assetService.saveAssetCredentials(vm.assetCredentials).then(function success(assetCredentials) {
            vm.assetCredentials = assetCredentials;
            $scope.theForm.$setPristine();
            $mdDialog.hide();
        });
    }
}
