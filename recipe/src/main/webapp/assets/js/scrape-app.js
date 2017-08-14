'use strict';

angular.module('myApp')
.directive('scrapeBox', function () {
	return {
		restrict: 'E',
		templateUrl: 'scrape-form.html'
	}
})

.directive('resultBox', function () {
	return {
		restrict: 'E',
		templateUrl: 'scrape-result.html'
	}
})

.directive('loader', function () {
	return {
		restrict: 'E',
		templateUrl: 'loader.html'
	}
})

.controller('scrapeController', function($http, $scope) {
      $scope.scholarshipData = [{
        "id": "SC01",
        "name": "Beasiswa Australia Awards",
        "options":[
            {   "id": "hasScholarshipName", "value": "Nama beasiswa", 
                "idPage": "P03", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/type-and-number-of-awards.htm"},
            
            {   "id": "hasSponsor", "value": "Pihak sponsor", 
                "idPage": "P01", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/about-australia-awards.htm"},
            
            {   "id": "hasManagedBy", "value": "Pengelola beasiswa", 
                "idPage": "P02", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/australia-awards-indonesia.htm"},
            
            {   "id": "scholarshipProvidesDegree", "value": "Jenjang pendidikan yang disediakan", 
                "idPage": "P03", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/type-and-number-of-awards.htm"},
            
            {   "id": "hasStartCourseIn", "value": "Tahun mulai perkuliahan", 
                "idPage": "P04", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/application-dates-and-timeline.htm"},
            
            {   "id": "givenToFieldStudy", "value": "Bidang studi yang tersedia", 
                "idPage": "P06", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/priority-fields-of-study.htm"},
            
            {   "id": "scholarshipForLocation", "value": "Tujuan negara", 
                "idPage": "P04", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/application-dates-and-timeline.htm"},
            
            {   "id": "hasApplicationDate", "value": "Waktu pendaftaran", 
                "idPage": "P04", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/application-dates-and-timeline.htm"},
            
            {   "id": "hasRegistrationAddress", "value": "Alamat pendaftaran", 
                "idPage": "P09", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/how-to-apply.htm"},
            
            {   "id": "hasSelectionProcess", "value": "Proses seleksi", 
                "idPage": "P05", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/selection-process.htm"},
            
            {   "id": "costsCover", "value": "Komponen biaya", 
                "idPage": "P07", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/scholarships-entitlement.htm"},
            
            {   "id": "requiresGPA", "value": "IPK minimal", 
                "idPage": "P08", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/category-and-eligibility.htm"},
            
            {   "id": "requiresTOEFL", "value": "Skor TOEFL minimal", 
                "idPage": "P08", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/category-and-eligibility.htm"},
            
            {   "id": "requiresIBT", "value": "Skor IBT minimal", 
                "idPage": "P08", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/category-and-eligibility.htm"},
            
            {   "id": "requiresIELTS", "value": "Skor IELTS minimal", 
                "idPage": "P08", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/category-and-eligibility.htm"},
            
            {   "id": "hasOfficeAddress", "value": "Alamat kantor", 
                "idPage": "P10", "page": "http://localhost/kk.thesis/Scraping/australiaawardsindo/contact-us.htm"}
        ]},
        
        { "id": "SC02",
        "name": "Beasiswa Pendidikan Indonesia (LPDP)",
        "options":[
            {   "id": "hasScholarshipName", "value": "Nama beasiswa", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "hasSponsor", "value": "Pihak sponsor", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "hasManagedBy", "value": "Pengelola beasiswa", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "scholarshipProvidesDegree", "value": "Jenjang pendidikan yang disediakan", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "givenToFieldStudy", "value": "Bidang studi yang tersedia", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "scholarshipForLocation", "value": "Tujuan negara", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "hasRegistrationAddress", "value": "Alamat pendaftaran", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "hasSelectionProcess", "value": "Proses seleksi", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "requiresDocumentForSelection", "value": "Persyaratan", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "costsCover", "value": "Komponen biaya", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "hasDurationOfScholarship", "value": "Masa studi", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "requiresAge", "value": "Usia maksimal", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "requiresGPA", "value": "IPK minimal", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "requiresTOEFL", "value": "Skor TOEFL minimal", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "requiresIBT", "value": "Skor IBT minimal", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "requiresIELTS", "value": "Skor IELTS minimal", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "hasOfficeAddress", "value": "Alamat kantor", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"},
            
            {   "id": "hasOfficialURL", "value": "Website resmi", 
                "idPage": "P11", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-pendidikan-indonesia.htm"}
        ]},
        
      { "id": "SC03",
        "name": "Beasiswa Afirmasi (LPDP)",
        "options":[
            {   "id": "hasScholarshipName", "value": "Nama beasiswa", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "hasManagedBy", "value": "Pengelola beasiswa", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "scholarshipProvidesDegree", "value": "Jenjang pendidikan yang disediakan", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "hasRegistrationAddress", "value": "Alamat pendaftaran", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "requiresDocumentForSelection", "value": "Persyaratan",
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "hasDurationOfScholarship", "value": "Masa studi", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "requiresAge", "value": "Usia maksimal", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "requiresGPA", "value": "IPK minimal", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "requiresTOEFL", "value": "Skor TOEFL minimal", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "hasOfficeAddress", "value": "Alamat kantor", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"},
            
            {   "id": "hasOfficialURL", "value": "Website resmi", 
                "idPage": "P12", "page": "http://localhost/kk.thesis/Scraping/beasiswalpdp/beasiswa-afirmasi.htm"}
        ]},
        
      { "id": "SC04",
        "name": "Beasiswa Monbukagakusho",
        "options":[
            {   "id": "hasScholarshipName", "value": "Nama beasiswa", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "hasSponsor", "value": "Pihak sponsor", 
                "idPage": "P14", "page": "http://www.id.emb-japan.go.jp/sch.html"},
            
            {   "id": "hasManagedBy", "value": "Pengelola beasiswa", 
                "idPage": "P14", "page": "http://www.id.emb-japan.go.jp/sch.html"},
            
            {   "id": "hasStartCourseIn", "value": "Tahun mulai perkuliahan",
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "scholarshipForLocation", "value": "Tujuan negara", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "hasApplicationDate", "value": "Waktu pendaftaran", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "hasRegistrationAddress", "value": "Alamat pendaftaran", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "hasSelectionProcess", "value": "Proses seleksi", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "requiresDocumentForSelection", "value": "Persyaratan", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "costsCover", "value": "Komponen biaya", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "hasScholarshipName3", "value": "Besaran beasiswa per bulan", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "requiresAge", "value": "Usia maksimal", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "requiresGPA", "value": "IPK minimal", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "requiresTOEFL", "value": "Skor TOEFL minimal", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "requiresIBT", "value": "Skor IBT minimal", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "requiresIELTS", "value": "Skor IELTS minimal", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "hasOfficeAddress", "value": "Alamat kantor", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"},
            
            {   "id": "hasOfficialURL", "value": "Website resmi", 
                "idPage": "P15", "page": "http://www.id.emb-japan.go.jp/sch_rs.html"}
        ]},
        
      { "id": "SC05",
        "name": "Beasiswa Kebijakan Publik (DAAD)",
        "options":[
            {   "id": "hasScholarshipName", "value": "Nama beasiswa", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "hasManagedBy", "value": "Pengelola beasiswa", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "scholarshipProvidesDegree", "value": "Jenjang pendidikan yang disediakan", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "hasStartCourseIn", "value": "Tahun mulai perkuliahan", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "givenToFieldStudy", "value": "Bidang studi yang tersedia", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "scholarshipForLocation", "value": "Tujuan negara", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "hasApplicationDate", "value": "Waktu pendaftaran", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "costsCover", "value": "Komponen biaya", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "hasScholarshipName3", "value": "Besaran beasiswa per bulan", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"},
            
            {   "id": "hasOfficialURL", "value": "Website resmi", 
                "idPage": "P16", "page": "http://www.scholars4dev.com/1010/daad-ms-scholarships-for-public-policy-and-good-governance-ppgg/"}
        ]},
        
      { "id": "SC06",
        "name": "Beasiswa Terkait Pembangunan (DAAD)",
        "options":[
            {   "id": "hasScholarshipName", "value": "Nama beasiswa", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "hasManagedBy", "value": "Pengelola beasiswa", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "scholarshipProvidesDegree", "value": "Jenjang pendidikan yang disediakan", 
                "idPage": "P18", "page": "https://www.daad.de/deutschland/stipendium/datenbank/en/21148-scholarship-database/?origin=5&status=3&subjectGrps=&daad=&q=epos&page=1&detail=10000008#prozess"},
            
            {   "id": "hasStartCourseIn", "value": "Tahun mulai perkuliahan", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "scholarshipForLocation", "value": "Tujuan negara", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "hasApplicationDate", "value": "Waktu pendaftaran", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "requiresDocumentForSelection", "value": "Persyaratan", 
                "idPage": "P18", "page": "https://www.daad.de/deutschland/stipendium/datenbank/en/21148-scholarship-database/?origin=5&status=3&subjectGrps=&daad=&q=epos&page=1&detail=10000008#prozess"},
            
            {   "id": "costsCover", "value": "Komponen biaya", 
                "idPage": "P18", "page": "https://www.daad.de/deutschland/stipendium/datenbank/en/21148-scholarship-database/?origin=5&status=3&subjectGrps=&daad=&q=epos&page=1&detail=10000008#prozess"},
            
            {   "id": "hasScholarshipName3", "value": "Besaran beasiswa per bulan", 
                "idPage": "P18", "page": "https://www.daad.de/deutschland/stipendium/datenbank/en/21148-scholarship-database/?origin=5&status=3&subjectGrps=&daad=&q=epos&page=1&detail=10000008#prozess"},
            
            {   "id": "requiresTOEFL", "value": "Skor TOEFL minimal", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "requiresIBT", "value": "Skor IBT minimal", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "requiresIELTS", "value": "Skor IELTS minimal", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"},
            
            {   "id": "hasOfficialURL", "value": "Website resmi", 
                "idPage": "P17", "page": "http://www.scholars4dev.com/1002/daad-scholarships-for-postgraduate-courses-with-special-relevance-to-developing-countries/"}
        ]}
        ];
  	      
      var _self = this;
		_self.serverAPI = 'swproject/scrape?id=';
		
		$scope.formIsSubmitted = false;
		$scope.loading = false;
		$scope.dataIsReady = false;
		$scope.mainAnswer = '';
		$scope.facts = [];
		  		
    	$scope.scrape = function(scholarship){
			$scope.loading = true;
			$scope.dataIsReady = false;
			$scope.mainAnswer = '';
    		$scope.facts = [];
    		$scope.id = scholarship.info.idPage;
			$scope.src = scholarship.info.page;
			$scope.i = scholarship.info.id;
			
			if ( $scope.loading ) {
				$http.get( _self.serverAPI + $scope.id + '&src=' + $scope.src + '&i=' + $scope.i)
				
				.then(function(res) {
					if ( res.data.code === 200) {
//    						alert('Found');
						var data = res.data.answer;
						$scope.mainAnswer = res.data.answer.text;
						$scope.facts = [];
						
						data.scrapeRes.forEach(function (value) {
							if( value !== null ) {
								var fact = {
									item: value.result,
									data:[]
								};	
								$scope.facts.push(fact);
							}
						});
			
					} else {
		//				alert('Can not found');
						$scope.mainAnswer = res.data.message;
					}
					
					$scope.loading = false;
					$scope.dataIsReady = true;
					
				});	
				
				console.log($scope.input)
			}	
    	};
});